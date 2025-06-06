package shop.sgmarket.sgmarketbackend.auction.application;

import java.util.List;
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
import shop.sgmarket.sgmarketbackend.auction.dto.response.PriceHistoryInfoResponse;
import shop.sgmarket.sgmarketbackend.auction.repository.ItemRepository;
import shop.sgmarket.sgmarketbackend.auction.repository.pricehistory.PriceHistoryRepository;
import shop.sgmarket.sgmarketbackend.auction.repository.auction.AuctionRepository;
import shop.sgmarket.sgmarketbackend.auction.repository.auctionLike.AuctionLikeRepository;
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
    private final AuctionLikeRepository auctionLikeRepository;
    private final PriceHistoryRepository priceHistoryRepository;

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
        auctionRepository.save(auction);

        return AuctionInfoResponse.of(auction, item, member, false);
    }

    @Transactional(readOnly = true)
    public AuctionInfoResponse getAuction(Long auctionId) {
        Member viewer = memberUtil.getCurrentMember();

        return auctionRepository.findAuctionInfoByIdAndStatus(
                auctionId, AuctionStatus.BIDDING, viewer
        ).orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public SliceResponse<AuctionInfoResponse> getOtherAuctionsBySameMember(
            Long auctionId,
            Pageable pageable
    ) {
        Auction auction = auctionRepository.findByIdAndStatus(auctionId, AuctionStatus.BIDDING)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));
        Member author = auction.getMember();

        Member viewer = memberUtil.getCurrentMember();

        Slice<AuctionInfoResponse> slice = auctionRepository.findAuctionInfoByMemberExcept(
                author,
                auctionId,
                AuctionStatus.BIDDING,
                pageable,
                viewer
        );

        return SliceResponse.from(slice);
    }


    @Transactional(readOnly = true)
    public SliceResponse<AuctionInfoResponse> getAuctionsByAddressAndCategory(String category, Pageable pageable) {
        Member member = memberUtil.getCurrentMember();

        Slice<AuctionInfoResponse> auctions = auctionRepository.findAuctionsWithinRadius(
                member.getLocation().getLatitude(),
                member.getLocation().getLongitude(),
                SEARCH_RADIUS_KM,
                AuctionCategory.fromKebabCase(category),
                pageable,
                member
        );

        return SliceResponse.from(auctions);
    }

    @Transactional(readOnly = true)
    public List<PriceHistoryInfoResponse> getPriceHistoryByAuctionId(Long auctionId) {
        Long itemId = auctionRepository.findItemIdByAuctionId(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        return priceHistoryRepository.findPriceHistoryInfoByItemId(itemId);
    }

    @Transactional(readOnly = true)
    public AuctionInfoResponse getRandomAuction() {
        Member viewer = memberUtil.getCurrentMember();

        return auctionRepository.findRandomAuctionInfoByStatus(
                AuctionStatus.BIDDING, viewer
        ).orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));
    }

    @Transactional
    public AuctionInfoResponse updateAuction(Long auctionId, AuctionUpdateRequest request, MultipartFile itemImage) {
        Member member = memberUtil.getCurrentMember();

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        validateAuthority(member, auction);

        String updatedImageUrl = auction.getImageUrl();
        if (itemImage != null && !itemImage.isEmpty()) {
            updatedImageUrl = getImageUrl(member, itemImage);
        }

        auction.update(
                request.title(),
                request.description(),
                updatedImageUrl,
                request.endDate(),
                AuctionCategory.from(request.auctionCategory())
        );

        auction.getItem().update(request.itemName());

        boolean isLiked = auctionLikeRepository.existsByAuctionAndMember(auction, member);

        return AuctionInfoResponse.of(auction, auction.getItem(), auction.getMember(), isLiked);
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
