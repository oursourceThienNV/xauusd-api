package com.group.samrt.um.controller;
import com.group.samrt.um.client.client.response.AccountReportResponse;
import com.group.samrt.um.client.client.response.TradeReportResponse;
import com.group.samrt.um.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;


    // =========================================================
    // DANH SÁCH ACCOUNT BÁO CÁO
    //
    // GET:
    // /api/reports/accounts
    //
    // PARAM:
    // page
    // size
    // keyword
    // from
    // to
    // =========================================================

    @GetMapping("/accounts")
    public ResponseEntity<Page<AccountReportResponse>> getAccountReports(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(required = false)
            String keyword,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to, @RequestParam(required = false) String roleType
    ) {

        // =====================================================
        // VALIDATE PAGE
        // =====================================================

        if (page < 0) {
            page = 0;
        }


        // =====================================================
        // VALIDATE SIZE
        // =====================================================

        if (size <= 0) {
            size = 10;
        }

        if (size > 100) {
            size = 100;
        }


        // =====================================================
        // VALIDATE DATE
        // =====================================================

        if (from == null || to == null) {

            throw new IllegalArgumentException(
                    "from và to không được để trống."
            );
        }

        if (from.isAfter(to)) {

            throw new IllegalArgumentException(
                    "Ngày bắt đầu không được lớn hơn ngày kết thúc."
            );
        }


        // =====================================================
        // FROM
        //
        // 2026-08-01 00:00:00
        // =====================================================

        Instant fromInstant =
                from.atStartOfDay(
                        ZoneId.systemDefault()
                ).toInstant();


        // =====================================================
        // TO
        //
        // + 1 ngày để lấy đủ ngày cuối
        //
        // 2026-08-24
        // =>
        // 2026-08-25 00:00:00
        // =====================================================

        Instant toInstant =
                to.plusDays(1)
                        .atStartOfDay(
                                ZoneId.systemDefault()
                        ).toInstant();


        // =====================================================
        // PAGINATION
        // =====================================================

        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                );


        // =====================================================
        // SERVICE
        // =====================================================

        Page<AccountReportResponse> result =
                reportService.getAccountReports(
                        fromInstant,
                        toInstant,
                        keyword,
                        roleType,
                        pageable
                );


        return ResponseEntity.ok(result);
    }


    // =========================================================
    // CHI TIẾT GIAO DỊCH
    //
    // GET:
    // /api/reports/accounts/{account}/trades
    //
    // PARAM:
    // page
    // size
    // keyword
    // from
    // to
    // =========================================================

    @GetMapping("/accounts/{account}/trades")
    public ResponseEntity<Page<TradeReportResponse>> getTradeReports(

            @PathVariable
            String account,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size,

            @RequestParam(required = false)
            String keyword,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to
    ) {

        // =====================================================
        // VALIDATE PAGE
        // =====================================================

        if (page < 0) {
            page = 0;
        }


        // =====================================================
        // VALIDATE SIZE
        // =====================================================

        if (size <= 0) {
            size = 20;
        }

        if (size > 100) {
            size = 100;
        }


        // =====================================================
        // VALIDATE ACCOUNT
        // =====================================================

        if (account == null
                || account.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Account không được để trống."
            );
        }


        // =====================================================
        // VALIDATE DATE
        // =====================================================

        if (from == null || to == null) {

            throw new IllegalArgumentException(
                    "from và to không được để trống."
            );
        }

        if (from.isAfter(to)) {

            throw new IllegalArgumentException(
                    "Ngày bắt đầu không được lớn hơn ngày kết thúc."
            );
        }


        // =====================================================
        // FROM
        // =====================================================

        Instant fromInstant =
                from.atStartOfDay(
                        ZoneId.systemDefault()
                ).toInstant();


        // =====================================================
        // TO
        // =====================================================

        Instant toInstant =
                to.plusDays(1)
                        .atStartOfDay(
                                ZoneId.systemDefault()
                        ).toInstant();


        // =====================================================
        // PAGINATION
        //
        // Giao dịch mới nhất lên đầu
        // =====================================================

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.DESC,
                                "closeTime"
                        )
                );


        // =====================================================
        // SERVICE
        // =====================================================

        Page<TradeReportResponse> result =
                reportService.getTradeReports(
                        account,
                        fromInstant,
                        toInstant,
                        keyword,
                        pageable
                );


        return ResponseEntity.ok(result);
    }
}