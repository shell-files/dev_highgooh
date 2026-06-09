package cloud.weareithero.api.inbound.service;

import org.springframework.stereotype.Service;

import cloud.weareithero.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InboundServiceImp implements InboundService {

    @Override
    public ResponseDTO findAll() {
        return null;
    }

    @Override
    public ResponseDTO findOne(int asnId) {
        return null;
    }
    
}
