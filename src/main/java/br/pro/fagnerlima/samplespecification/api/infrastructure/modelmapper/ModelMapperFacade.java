package br.pro.fagnerlima.samplespecification.api.infrastructure.modelmapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ModelMapperFacade {

    private ModelMapper modelMapper;

    public ModelMapperFacade(ModelMapper modelMapper) {
        super();
        this.modelMapper = modelMapper;
    }

    public <T> T map(Object data, Class<T> destinationType) {
        return modelMapper.map(data, destinationType);
    }

    public <T> Page<T> map(Page<?> dataPage, Class<T> destinationType) {
        return dataPage.map(data -> map(data, destinationType));
    }

    public <T> List<T> map(List<?> dataList, Class<T> destinationType) {
        return dataList.stream().map(data -> map(data, destinationType)).collect(Collectors.toList());
    }

}
