package shop.sgmarket.sgmarketbackend.auction.application;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import shop.sgmarket.sgmarketbackend.global.dto.PageResponse;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;
import shop.sgmarket.sgmarketbackend.global.service.S3UploadService;
import shop.sgmarket.sgmarketbackend.global.util.MemberUtil;
import shop.sgmarket.sgmarketbackend.member.domain.Member;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final ItemRepository itemRepository;
    private final AuctionRepository auctionRepository;
    private final AuctionCategoryRepository auctionCategoryRepository;
    private final MemberUtil memberUtil;
    private final S3UploadService s3UploadService;

    @Transactional
    public AuctionInfoResponse register(AuctionRegisterRequest request, MultipartFile itemImage) throws IOException {
        Member member = memberUtil.getCurrentMember();

        String imageUrl = s3UploadService.uploadImage(itemImage, "auction/" + member.getId());
        Item item = Item.createItem(request.itemRegisterRequest().itemName(), member);

        AuctionCategory auctionCategory = auctionCategoryRepository.findByName(request.auctionCategory())
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_CATEGORY_NOT_FOUND));

        Auction auction = Auction.create(
                request.title(),
                request.description(),
                imageUrl,
                request.endDate(),
                request.startPrice(),
                request.currentPrice(),
                request.endPrice(),
                auctionCategory,
                item
        );

        itemRepository.save(item);
        auctionRepository.save(auction);

        return AuctionInfoResponse.of(auction, item, member);
    }

    @Transactional(readOnly = true)
    public AuctionInfoResponse getAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        return AuctionInfoResponse.of(auction, auction.getItem(), auction.getItem().getMember());
    }

    @Transactional(readOnly = true)
    public PageResponse<AuctionInfoResponse> getAllAuctions(Pageable pageable) {
        Page<Auction> auctions = auctionRepository.findAll(pageable);

        Page<AuctionInfoResponse> auctionInfoResponses = auctions.map(auction ->
                AuctionInfoResponse.of(
                        auction,
                        auction.getItem(),
                        auction.getItem().getMember()
                )
        );

        return PageResponse.from(auctionInfoResponses);
    }

    @Transactional
    public AuctionInfoResponse updateAuction(Long auctionId, AuctionUpdateRequest request) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        AuctionCategory auctionCategory = auctionCategoryRepository.findByName(request.auctionCategory())
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_CATEGORY_NOT_FOUND));

        auction.update(
                request.title(),
                request.description(),
                request.imageUrl(),
                request.endDate(),
                auctionCategory
        );

        return AuctionInfoResponse.of(auction, auction.getItem(), auction.getItem().getMember());
    }

    @Transactional
    public void deleteAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));
        auction.delete();
    }
}
