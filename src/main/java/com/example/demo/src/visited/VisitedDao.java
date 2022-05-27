package com.example.demo.src.visited;

import com.example.demo.src.review.model.PostReviewListReq;
import com.example.demo.src.visited.model.PostVisitedReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;

@Repository
public class VisitedDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createVisited(PostVisitedReq postVisitedReq) {
        String createVisitedQuery="insert into Visited(storeId,userId,isPublic) values(?,?,?)";
        Object[] createVisitedParam=new Object[]{
                postVisitedReq.getStoreId(),postVisitedReq.getUserId(),postVisitedReq.getIsPublic()};

        this.jdbcTemplate.update(createVisitedQuery,createVisitedParam);
    }

    public int checkDate(Long storeId, Long userId) {
        String checkDateQuery = "select exists(select Visited.id from Visited where storeId=? && userId=? && DATE(createdAt) = ?)";
        return this.jdbcTemplate.queryForObject(checkDateQuery, int.class, storeId, userId, LocalDate.now());
    }
}
