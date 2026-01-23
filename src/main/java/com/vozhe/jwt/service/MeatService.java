package com.vozhe.jwt.service;

import com.vozhe.jwt.models.Meat;

import java.util.List;

public interface MeatService {
    List<Meat> findAll();
    Meat findById(Long id);
    Meat save(Meat meat);
    void deleteById(Long id);
}
