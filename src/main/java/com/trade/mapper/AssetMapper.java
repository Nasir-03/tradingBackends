package com.trade.mapper;

import org.springframework.context.annotation.Configuration;

import com.trade.DTO.AssetDTO;
import com.trade.modal.Asset;

@Configuration
public class AssetMapper {

	public AssetDTO toDto(Asset asset) {
	    AssetDTO dto = new AssetDTO();
	    dto.setId(asset.getId());
	    dto.setQuantity(asset.getQuantity());
	    dto.setBuyPrice(asset.getBuyPrice());
	    dto.setUser(asset.getUser());
	    
	    return dto;
	}

	public Asset toEntity(AssetDTO dto) {
       Asset asset = new Asset();
       
       asset.setId(dto.getId());
       asset.setQuantity(dto.getQuantity());
       asset.setBuyPrice(dto.getBuyPrice());
       asset.setUser(dto.getUser());
       
       return asset;
	}
}
