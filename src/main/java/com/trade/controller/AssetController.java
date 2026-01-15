package com.trade.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trade.DTO.AssetDTO;
import com.trade.config.JwtTokenValidator;
import com.trade.modal.Asset;
import com.trade.modal.User;
import com.trade.service.AssetService;
import com.trade.service.UserService;

@RestController
@RequestMapping("/asset")
public class AssetController {

    private final JwtTokenValidator jwtTokenValidator;

	private AssetService assetService;
	private UserService userService;
	
	@Autowired
	public AssetController(AssetService assetService, UserService userService, JwtTokenValidator jwtTokenValidator) {
		this.assetService = assetService;
		this.userService = userService;
		this.jwtTokenValidator = jwtTokenValidator;
	}
	
	@GetMapping("getAsset/{assetId}")
	public ResponseEntity<Asset> getAssetById(@PathVariable Long assetId){
		Asset asset = assetService.getAssetById(assetId);
		return ResponseEntity.ok().body(asset);
	}
	
	@GetMapping("/coin/{coinId}/user")
	public ResponseEntity<Asset> getAssetByUserIdAndCoinId(
			@PathVariable String coinId,
			@RequestHeader("Authorization") String jwt
			) throws Exception{
		
		User user = userService.getUserByJwt(jwt);
		Asset asset = assetService.findAssetByUserIdAndCoinId(user.getId(), coinId);
		
		return ResponseEntity.ok().body(asset);
	}
	
	@GetMapping("/getUserAsset")
	public ResponseEntity<List<Asset>> getAssetByUser(@RequestHeader("Authorization") String jwt) throws Exception{
		
		User user = userService.getUserByJwt(jwt);
		
		List<Asset> assets = assetService.getUserAssets(user.getId());
		
		return ResponseEntity.ok().body(assets);
	}
}
