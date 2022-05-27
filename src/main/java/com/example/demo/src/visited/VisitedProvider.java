package com.example.demo.src.visited;

import com.example.demo.src.review.ReviewDao;
import com.example.demo.src.store.StoreDao;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VisitedProvider {
    private final VisitedDao visitedDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public VisitedProvider(VisitedDao visitedDao, JwtService jwtService) {
        this.visitedDao = visitedDao;
        this.jwtService = jwtService;
    }
}
