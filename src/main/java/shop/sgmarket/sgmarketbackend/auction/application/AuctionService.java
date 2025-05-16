package shop.sgmarket.sgmarketbackend.auction.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import shop.sgmarket.sgmarketbackend.auction.domain.Auction;
import shop.sgmarket.sgmarketbackend.auction.domain.AuctionCategory;
import shop.sgmarket.sgmarketbackend.auction.domain.AuctionStatus;
import shop.sgmarket.sgmarketbackend.auction.domain.Item;
import shop.sgmarket.sgmarketbackend.auction.dto.request.AuctionRegisterRequest;
import shop.sgmarket.sgmarketbackend.auction.dto.request.AuctionUpdateRequest;
import shop.sgmarket.sgmarketbackend.auction.dto.response.AuctionInfoResponse;
import shop.sgmarket.sgmarketbackend.auction.repository.AuctionRepository;
import shop.sgmarket.sgmarketbackend.auction.repository.ItemRepository;
import shop.sgmarket.sgmarketbackend.global.dto.SliceResponse;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;
import shop.sgmarket.sgmarketbackend.global.service.S3UploadService;
import shop.sgmarket.sgmarketbackend.global.util.MemberUtil;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private static final Double SEARCH_RADIUS_KM = 10.0;
    private static final String AUCTION_IMAGE_PATH = "auction/";

    private final ItemRepository itemRepository;
    private final AuctionRepository auctionRepository;
    private final MemberUtil memberUtil;
    private final S3UploadService s3UploadService;

    @Transactional
    public AuctionInfoResponse register(AuctionRegisterRequest request, MultipartFile itemImage) {
        Member member = memberUtil.getCurrentMember();

        String imageUrl = getImageUrl(member, itemImage);
        String itemName = request.itemRegisterRequest().itemName();
        Item item = itemRepository.findByName(itemName)
                .orElseGet(() -> itemRepository.save(Item.createItem(itemName)));

        Auction auction = Auction.create(
                request.title(),
                request.description(),
                imageUrl,
                request.endDate(),
                request.startPrice(),
                member.getLocation().getLatitude(),
                member.getLocation().getLongitude(),
                AuctionCategory.from(request.auctionCategory()),
                item,
                member
        );

        itemRepository.save(item);
        auctionRepository.save(auction);

        return AuctionInfoResponse.of(auction, item, member);
    }

    @Transactional(readOnly = true)
    public AuctionInfoResponse getAuction(Long auctionId) {
        Auction auction = auctionRepository.findByIdAndStatus(auctionId, AuctionStatus.BIDDING)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        return AuctionInfoResponse.of(auction, auction.getItem(), auction.getMember());
    }

    @Transactional(readOnly = true)
    public SliceResponse<AuctionInfoResponse> getOtherAuctionsBySameMember(Long auctionId, Pageable pageable) {
        Auction auction = auctionRepository.findByIdAndStatus(auctionId, AuctionStatus.BIDDING)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));
        Member author = auction.getMember();

        Slice<Auction> auctions = auctionRepository.findAllByMemberAndStatusAndIdNot(author, AuctionStatus.BIDDING,
                auctionId, pageable);

        Slice<AuctionInfoResponse> auctionInfoResponses = auctions.map(otherAuction ->
                AuctionInfoResponse.of(otherAuction, otherAuction.getItem(), author)
        );

        return SliceResponse.from(auctionInfoResponses);
    }

    @Transactional(readOnly = true)
    public SliceResponse<AuctionInfoResponse> getAuctionsByAddressAndCategory(String category, Pageable pageable) {
        Member member = memberUtil.getCurrentMember();

        Slice<Auction> auctions = auctionRepository.findAuctionsWithinRadius(
                member.getLocation().getLatitude(),
                member.getLocation().getLongitude(),
                SEARCH_RADIUS_KM,
                AuctionCategory.fromKebabCase(category),
                pageable
        );

        Slice<AuctionInfoResponse> auctionInfoResponses = auctions.map(auction ->
                AuctionInfoResponse.of(
                        auction,
                        auction.getItem(),
                        auction.getMember()
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

        auction.update(
                request.title(),
                request.description(),
                getImageUrl(member, itemImage),
                request.endDate(),
                AuctionCategory.from(request.auctionCategory())
        );

        auction.getItem().update(request.itemName());

        return AuctionInfoResponse.of(auction, auction.getItem(), auction.getMember());
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
        return s3UploadService.uploadImage(itemImage, AUCTION_IMAGE_PATH + member.getId());
    }

    private void validateAuthority(Member member, Auction auction) {
        if (!member.getId().equals(auction.getMember().getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER);
        }
    }
}
