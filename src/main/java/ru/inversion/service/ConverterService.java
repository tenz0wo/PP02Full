package ru.inversion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.inversion.model.RequestParams;
import ru.inversion.model.ResponseObj;
import ru.inversion.repo.ConverterRepository;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;


@Service
public class ConverterService {

    protected ConverterRepository converterRepository;

    @Autowired
    public ConverterService(ConverterRepository converterRepository) {
        this.converterRepository = converterRepository;
    }

    public ResponseObj getConvert(RequestParams params) throws SQLException{
        return converterRepository.getConvert(params);
    }

    public ResponseObj getConvertUi(RequestParams params) throws SQLException{
        return converterRepository.getConvertUi(params);
    }
}