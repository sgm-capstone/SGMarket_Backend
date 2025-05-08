package shop.sgmarket.sgmarketbackend.auction.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import shop.sgmarket.sgmarketbackend.auction.domain.Auction;
import shop.sgmarket.sgmarketbackend.auction.domain.AuctionCategory;
import shop.sgmarket.sgmarketbackend.auction.domain.Item;
import shop.sgmarket.sgmarketbackend.auction.dto.request.AuctionRegisterRequest;
import shop.sgmarket.sgmarketbackend.auction.dto.request.AuctionUpdateRequest;
import shop.sgmarket.sgmarketbackend.auction.dto.response.AuctionInfoResponse;
import shop.sgmarket.sgmarketbackend.auction.repository.AuctionCategoryRepository;
import shop.sgmarket.sgmarketbackend.auction.repository.AuctionRepository;
import shop.sgmarket.sgmarketbackend.auction.repository.ItemRepository;
import shop.sgmarket.sgmarketbackend.global.domain.Status;
import shop.sgmarket.sgmarketbackend.global.dto.SliceResponse;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;
import shop.sgmarket.sgmarketbackend.global.service.S3UploadService;
import shop.sgmarket.sgmarketbackend.global.util.MemberUtil;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private static final double SEARCH_RADIUS_KM = 10.0;

    private final ItemRepository itemRepository;
    private final AuctionRepository auctionRepository;
    private final AuctionCategoryRepository auctionCategoryRepository;
    private final MemberUtil memberUtil;
    private final S3UploadService s3UploadService;

    @Transactional
    public AuctionInfoResponse register(AuctionRegisterRequest request, MultipartFile itemImage) {
        Member member = memberUtil.getCurrentMember();

        String imageUrl = getImageUrl(member, itemImage);
        Item item = Item.createItem(request.itemRegisterRequest().itemName(), imageUrl, member);

        AuctionCategory auctionCategory = auctionCategoryRepository.findByName(request.auctionCategory())
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_CATEGORY_NOT_FOUND));

        Auction auction = Auction.create(
                request.title(),
                request.description(),
                request.endDate(),
                request.startPrice(),
                request.currentPrice(),
                request.endPrice(),
                member.getLocation().getLatitude(),
                member.getLocation().getLongitude(),
                auctionCategory,
                item
        );

        itemRepository.save(item);
        auctionRepository.save(auction);

        return AuctionInfoResponse.of(auction, item, member);
    }

    @Transactional(readOnly = true)
    public AuctionInfoResponse getAuction(Long auctionId) {
        Auction auction = auctionRepository.findByIdAndStatus(auctionId, Status.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        return AuctionInfoResponse.of(auction, auction.getItem(), auction.getItem().getMember());
    }

    @Transactional(readOnly = true)
    public SliceResponse<AuctionInfoResponse> getAuctionsByAddressAndCategory(String category, Pageable pageable) {
        Member member = memberUtil.getCurrentMember();
        Slice<Auction> auctions = auctionRepository.findAuctionsWithinRadius(
                member.getLocation().getLatitude(),
                member.getLocation().getLongitude(),
                SEARCH_RADIUS_KM,
                Status.ACTIVE,
                category,
                pageable
        );

        Slice<AuctionInfoResponse> auctionInfoResponses = auctions.map(auction ->
                AuctionInfoResponse.of(
                        auction,
                        auction.getItem(),
                        auction.getItem().getMember()
                )
        );

        return SliceResponse.from(auctionInfoResponses);
    }

    @Transactional
    public AuctionInfoResponse updateAuction(Long auctionId, AuctionUpdateRequest request, MultipartFile itemImage) {
        Member member = memberUtil.getCurrentMember();

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        validateAuthority(member, auction);
        AuctionCategory auctionCategory = auctionCategoryRepository.findByName(request.auctionCategory())
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_CATEGORY_NOT_FOUND));

        auction.update(
                request.title(),
                request.description(),
                request.endDate(),
                auctionCategory
        );

        String image = getImageUrl(member, itemImage);
        auction.getItem().update(request.itemName(), image);

        return AuctionInfoResponse.of(auction, auction.getItem(), auction.getItem().getMember());
    }

    @Transactional
    public void deleteAuction(Long auctionId) {
        Member member = memberUtil.getCurrentMember();
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        validateAuthority(member, auction);
        auction.delete();
    }

    private String getImageUrl(Member member, MultipartFile itemImage) {
        return s3UploadService.uploadImage(itemImage, "auction/" + member.getId());
    }

    private void validateAuthority(Member member, Auction auction) {
        if (!member.getId().equals(auction.getItem().getMember().getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER);
        }
    }
}
