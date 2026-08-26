package com.group.samrt.um.controller;

import com.group.samrt.um.client.client.response.AccountListResponse;
import com.group.samrt.um.domain.uml.AdminUser;
import com.group.samrt.um.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;


    /**
     * Danh sách tài khoản
     *
     * GET /api/accounts
     *
     * GET /api/accounts?page=0&size=10
     *
     * GET /api/accounts?page=0&size=10&keyword=123456
     */
    @GetMapping
    public ResponseEntity<Page<AccountListResponse>> getAccounts(

            @RequestParam(
                    required = false
            )
            String keyword,

            @RequestParam(
                    defaultValue = "0"
            )
            int page,

            @RequestParam(
                    defaultValue = "10"
            )
            int size

    ) {

        // Không cho request size quá lớn
        if (size > 100) {
            size = 100;
        }

        if (size < 1) {
            size = 10;
        }

        if (page < 0) {
            page = 0;
        }

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.ASC,
                                "username"
                        )
                );

        Page<AccountListResponse> result =
                userService.getAccountList(
                        keyword,
                        pageable
                );

        return ResponseEntity.ok(result);
    }
    // =========================================================
// BLOCK ACCOUNT
// =========================================================

    @PostMapping("/{username}/block")
    public ResponseEntity<?> blockAccount(
            @PathVariable String username
    ) {

        AdminUser user =
                userService.blockAccount(username);

        return ResponseEntity.ok(user);
    }


// =========================================================
// UNBLOCK ACCOUNT
// =========================================================

    @PostMapping("/{username}/unblock")
    public ResponseEntity<?> unblockAccount(
            @PathVariable String username
    ) {

        AdminUser user =
                userService.unblockAccount(username);

        return ResponseEntity.ok(user);
    }


// =========================================================
// RENEW ACCOUNT
// =========================================================

    @PostMapping("/{username}/renew")
    public ResponseEntity<?> renewAccount(
            @PathVariable String username,
            @RequestParam int days
    ) {

        AdminUser user =
                userService.renewAccount(
                        username,
                        days
                );

        return ResponseEntity.ok(user);
    }
}