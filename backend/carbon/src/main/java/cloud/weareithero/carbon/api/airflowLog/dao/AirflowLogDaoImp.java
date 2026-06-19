package cloud.weareithero.carbon.api.airflowLog.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import cloud.weareithero.carbon.api.airflowLog.dto.AirflowLogDTO;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AirflowLogDaoImp implements AirflowLogDao {

    private final AirflowLogMapper airflowLogMapper;

    @Override
    public List<AirflowLogDTO> selectByTargetDay(String targetDay) {
        return airflowLogMapper.selectByTargetDay(targetDay);
    }
}