package com.trade.service;

import java.util.List;

import com.trade.DTO.AssetDTO;
import com.trade.modal.Asset;
import com.trade.modal.Bitcoin;
import com.trade.modal.User;

public interface AssetService {

	Asset createAsset(User user, Bitcoin coin,double quantity);
	
	Asset getAssetById(Long assetId);
	
	AssetDTO getAssetByUserIdAndId(Long userId,String assetId);
	
	List<Asset> getUserAssets(Long userId);
	
	Asset updateAsset(Long assetId,double quantity);
	
	Asset findAssetByUserIdAndCoinId(Long userId,String coinId);
	
	void deleteAsset(Long assetId);
}
