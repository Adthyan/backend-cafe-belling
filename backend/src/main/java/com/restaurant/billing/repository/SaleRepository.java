package com.restaurant.billing.repository;

import com.restaurant.billing.entity.Sale;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query(
            "select cast(s.soldAt as date), count(s), coalesce(sum(s.total), 0) from Sale s "
                    + "where year(s.soldAt) = :year and month(s.soldAt) = :month "
                    + "group by cast(s.soldAt as date) order by 1")
    List<Object[]> dailyAggregates(@Param("year") int year, @Param("month") int month);

    @Query(
            "select count(s), coalesce(sum(s.total), 0) from Sale s "
                    + "where year(s.soldAt) = :year and month(s.soldAt) = :month")
    List<Object[]> monthTotals(@Param("year") int year, @Param("month") int month);
}
