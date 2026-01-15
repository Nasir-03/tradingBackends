package com.trade.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trade.DTO.AssetDTO;
import com.trade.extra.SequenceGeneratorService;
import com.trade.mapper.AssetMapper;
import com.trade.modal.Asset;
import com.trade.modal.Bitcoin;
import com.trade.modal.User;
import com.trade.repositoy.AssetRepository;

@Service
public class AssetServiceImpl implements AssetService{

	private AssetRepository assetRepository;
	private AssetMapper assetMapper;
	private CoinServiceImpl coinServiceImpl;
	private SequenceGeneratorService sequenceGeneratorService;
	
	public AssetServiceImpl(AssetRepository assetRepository,AssetMapper assetMapper,
			CoinServiceImpl coinServiceImpl,SequenceGeneratorService sequenceGeneratorService) {
		this.assetRepository = assetRepository;
		this.assetMapper = assetMapper;
		this.coinServiceImpl = coinServiceImpl;
		this.sequenceGeneratorService = sequenceGeneratorService;
	}

//	@Override
//	public Asset createAsset(User user, Bitcoin coin, double quantity) {
//		Asset asset = new Asset();
//		
//		asset.setUser(user);
//		asset.setCoin(coin);
//		asset.setQuantity(quantity);
//		asset.setBuyPrice(coin.getCurrentPrice());
//		return assetRepository.save(asset);
//	}
	
//	@Transactional
//	public Asset createAsset(User user, Bitcoin coin, double quantity) {
//	    Bitcoin dbCoin = coinServiceImpl.ensureCoinExists(coin);
//
//	    Asset asset = new Asset();
//	    asset.setUser(user);
//	    asset.setCoin(dbCoin);
//	    asset.setQuantity(quantity);
//	    asset.setBuyPrice(dbCoin.getCurrentPrice());
//	    return assetRepository.saveAndFlush(asset);
//	}
	
	@Transactional
	public Asset createAsset(User user, Bitcoin coin, double quantity) {

	    Asset asset = new Asset();
	    asset.setId(sequenceGeneratorService.generateSequence("asset_sequence"));
	    asset.setUser(user);
	    asset.setCoin(coin);   // already DB-managed
	    asset.setQuantity(quantity);
	    asset.setBuyPrice(coin.getCurrentPrice());

	    return assetRepository.save(asset);
	}


	@Override
	public Asset getAssetById(Long assetId) {
		return assetRepository.findById(assetId)
				.orElseThrow(()-> new RuntimeException("Asset not found"));
	}

	@Override
	public AssetDTO getAssetByUserIdAndId(Long userId, String assetId) {
		Asset asset = assetRepository.findByUserIdAndCoinId(userId, assetId);
	
	     return assetMapper.toDto(asset);	
	}

	@Override
	public List<Asset> getUserAssets(Long userId) {
		return assetRepository.findByUserId(userId);
	}

	@Override
	public Asset updateAsset(Long assetId, double quantity) {
		Asset oldAsset = getAssetById(assetId);
		
		oldAsset.setQuantity(quantity + oldAsset.getQuantity());
		
		return assetRepository.save(oldAsset);
	}

	@Override
	public Asset findAssetByUserIdAndCoinId(Long userId, String coinId) {
		Asset asset = assetRepository.findByUser_IdAndCoin_Id(userId, coinId);
	
	    return asset;
	}

	@Override
	public void deleteAsset(Long assetId) {
		assetRepository.deleteById(assetId);
	}

}
