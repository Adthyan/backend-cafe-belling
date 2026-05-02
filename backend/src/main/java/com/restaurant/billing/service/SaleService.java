package com.restaurant.billing.service;

import com.restaurant.billing.dto.CheckoutLineRequest;
import com.restaurant.billing.dto.CheckoutLineResponse;
import com.restaurant.billing.dto.CheckoutRequest;
import com.restaurant.billing.dto.CheckoutResponse;
import com.restaurant.billing.dto.DailySaleDto;
import com.restaurant.billing.dto.MonthlyReportResponse;
import com.restaurant.billing.entity.MenuItem;
import com.restaurant.billing.entity.Sale;
import com.restaurant.billing.entity.SaleLine;
import com.restaurant.billing.entity.ShopSettings;
import com.restaurant.billing.repository.MenuItemRepository;
import com.restaurant.billing.repository.SaleRepository;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final MenuItemRepository menuItemRepository;
    private final ShopSettingsService shopSettingsService;
    private final UpiPaymentService upiPaymentService;

    public SaleService(
            SaleRepository saleRepository,
            MenuItemRepository menuItemRepository,
            ShopSettingsService shopSettingsService,
            UpiPaymentService upiPaymentService) {
        this.saleRepository = saleRepository;
        this.menuItemRepository = menuItemRepository;
        this.shopSettingsService = shopSettingsService;
        this.upiPaymentService = upiPaymentService;
    }

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest request) {
        Map<Long, Integer> qtyByItem = new HashMap<>();
        for (CheckoutLineRequest line : request.getLines()) {
            qtyByItem.merge(line.getMenuItemId(), line.getQuantity(), Integer::sum);
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        List<CheckoutLineResponse> responseLines = new ArrayList<>();
        List<SaleLine> saleLines = new ArrayList<>();
        Sale sale = new Sale();
        sale.setTax(BigDecimal.ZERO);
        sale.setPaymentNote("AWAITING_GATEWAY");
        sale.setPaymentStatus("PENDING");

        for (Map.Entry<Long, Integer> e : qtyByItem.entrySet()) {
            Long menuItemId = e.getKey();
            int qty = e.getValue();
            MenuItem item =
                    menuItemRepository
                            .findById(menuItemId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown menu item: " + menuItemId));
            if (!item.isActive()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inactive menu item: " + menuItemId);
            }
            BigDecimal unit = item.getPrice();
            BigDecimal lineTotal = unit.multiply(BigDecimal.valueOf(qty));
            subtotal = subtotal.add(lineTotal);

            SaleLine sl = new SaleLine();
            sl.setSale(sale);
            sl.setMenuItem(item);
            sl.setQuantity(qty);
            sl.setUnitPrice(unit);
            sl.setLineTotal(lineTotal);
            saleLines.add(sl);

            responseLines.add(new CheckoutLineResponse(item.getId(), item.getName(), qty, unit, lineTotal));
        }

        sale.setSubtotal(subtotal);
        sale.setTotal(subtotal);
        sale.getLines().addAll(saleLines);
        Sale saved = saleRepository.save(sale);

        ShopSettings settings = shopSettingsService.getOrCreate();
        String upiUri = upiPaymentService.buildUpiUri(settings, saved.getTotal(), "Bill " + saved.getId());

        CheckoutResponse resp = new CheckoutResponse();
        resp.setSaleId(saved.getId());
        resp.setSoldAt(saved.getSoldAt());
        resp.setLines(responseLines);
        resp.setSubtotal(saved.getSubtotal());
        resp.setTax(saved.getTax());
        resp.setTotal(saved.getTotal());
        resp.setUpiUri(upiUri);
        return resp;
    }

    @Transactional(readOnly = true)
    public String upiUriForSale(Long saleId) {
        Sale sale =
                saleRepository
                        .findById(saleId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sale not found"));
        ShopSettings settings = shopSettingsService.getOrCreate();
        return upiPaymentService.buildUpiUri(settings, sale.getTotal(), "Bill " + sale.getId());
    }

    @Transactional
    public void markPaid(Long saleId) {
        Sale sale =
                saleRepository
                        .findById(saleId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sale not found"));
        sale.setPaymentStatus("PAID");
        saleRepository.save(sale);
    }

    @Transactional(readOnly = true)
    public MonthlyReportResponse monthlyReport(int year, int month) {
        if (month < 1 || month > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "month must be 1-12");
        }
        List<Object[]> totalRows = saleRepository.monthTotals(year, month);
        Object[] totals = totalRows.isEmpty() ? new Object[] {0L, BigDecimal.ZERO} : totalRows.get(0);
        long monthCount = ((Number) totals[0]).longValue();
        BigDecimal monthRevenue = totals[1] != null ? new BigDecimal(totals[1].toString()) : BigDecimal.ZERO;

        List<Object[]> rows = saleRepository.dailyAggregates(year, month);
        List<DailySaleDto> daily = new ArrayList<>();
        for (Object[] row : rows) {
            LocalDate d;
            Object dateVal = row[0];
            if (dateVal instanceof Date sqlDate) {
                d = sqlDate.toLocalDate();
            } else if (dateVal instanceof java.time.LocalDate ld) {
                d = ld;
            } else {
                d = LocalDate.parse(dateVal.toString());
            }
            long cnt = ((Number) row[1]).longValue();
            BigDecimal rev = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;
            daily.add(new DailySaleDto(d, cnt, rev));
        }

        MonthlyReportResponse r = new MonthlyReportResponse();
        r.setYear(year);
        r.setMonth(month);
        r.setTotalSaleCount(monthCount);
        r.setTotalRevenue(monthRevenue);
        r.setDailyBreakdown(daily);
        return r;
    }
}
