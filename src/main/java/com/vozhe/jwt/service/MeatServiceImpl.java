package com.vozhe.jwt.service;

import com.vozhe.jwt.models.Meat;
import com.vozhe.jwt.models.OutputCut;
import com.vozhe.jwt.repository.MeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeatServiceImpl implements MeatService {

    private final MeatRepository meatRepository;

    @Override
    public List<Meat> findAll() {
        return meatRepository.findAllWithOutputCuts();  // ← Use the new method
    }

    @Override
    public Meat findById(Long id) {
        return meatRepository.findById(id).orElse(null);
    }

    @Override
    public Meat save(Meat meat) {
        return meatRepository.save(meat);
    }

    @Override
    public void deleteById(Long id) {
        meatRepository.deleteById(id);
    }
}
