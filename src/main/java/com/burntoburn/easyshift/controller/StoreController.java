package com.burntoburn.easyshift.controller;

import com.burntoburn.easyshift.common.response.ApiResponse;
import com.burntoburn.easyshift.dto.store.use.*;
import com.burntoburn.easyshift.service.login.CustomUserDetails;
import com.burntoburn.easyshift.service.store.StoreService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    /**
     * 매장 생성 API
     */
    @PostMapping("/stores")
    public ResponseEntity<ApiResponse<StoreCreateResponse>> createStore(@Valid @RequestBody StoreCreateRequest request,
                                                                        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getId();
        StoreCreateResponse response = storeService.createStore(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    // ========================================

    /**
     * 매장 목록 조회 API
     */
    @GetMapping("/stores")
    public ResponseEntity<ApiResponse<UserStoresResponse>> getUserStores(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        UserStoresResponse response = storeService.getUserStores(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ========================================

    /**
     * 매장 조회 API
     */
    @GetMapping("/stores/{storeId}")
    public ResponseEntity<ApiResponse<StoreInfoResponse>> getStore(@PathVariable Long storeId,
                                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        // UserId는 spring security의 @AuthenticationPrincipal로 받아올 수 있음
        // Long userId = userDetails.getUserId();

        Long userId = userDetails.getUser().getId(); // 여기서는 임의로 1로 설정
        StoreInfoResponse response = storeService.getStoreInfo(storeId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    // ========================================

    /**
     * 매장 정보 수정 API
     */
    @PatchMapping("/stores/{storeId}")
    public ResponseEntity<ApiResponse<Void>> updateStore(@PathVariable Long storeId, @Valid @RequestBody StoreUpdateRequest request) {
        storeService.updateStore(storeId, request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    // ========================================

    /**
     * 매장 삭제 API
     */
    @DeleteMapping("/stores/{storeId}")
    public ResponseEntity<ApiResponse<Void>> deleteStore(@PathVariable Long storeId) {
        storeService.deleteStore(storeId);
        return ResponseEntity.ok(ApiResponse.success());
    }
    // ========================================

    /**
     * 매장 사용자 목록 조회 API
     */
    @GetMapping("stores/{storeId}/users")
    public ResponseEntity<ApiResponse<StoreUsersResponse>> getStoreUsers(@PathVariable Long storeId) {
        StoreUsersResponse response = storeService.getStoreUsers(storeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ========================================


    /**
     * 매장 정보 조회 API
     */
    @GetMapping("/stores/info")
    public ResponseEntity<ApiResponse<StoreResponse>> getStoreSimpleInfo(@RequestParam UUID storeCode) {
        StoreResponse response = storeService.getStoreSimpleInfo(storeCode);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    // ========================================

    /**
     * 매장 입장 API
     */
    @PostMapping("/stores/join")
    public ResponseEntity<ApiResponse<Void>> getStore(@RequestParam UUID storeCode,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUser().getId(); // 여기서는 임의로 1로 설정

        storeService.joinUserStore(storeCode, userId);

        return ResponseEntity.ok(ApiResponse.success());
    }


}
