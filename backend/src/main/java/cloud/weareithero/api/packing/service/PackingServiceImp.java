package cloud.weareithero.api.packing.service;

import org.springframework.stereotype.Service;

import cloud.weareithero.api.packing.dto.PackingRequestDTO;
import cloud.weareithero.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PackingServiceImp implements PackingService {
    
    @Override
    public ResponseDTO findAll(PackingRequestDTO packingRequestDTO) {
        return null;
    }
}
