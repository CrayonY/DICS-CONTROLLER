package com.ucd.server.mapper;

import com.ucd.server.model.TdhTaskParameter;

import java.util.List;
import java.util.Map;

public interface TdhTaskParameterMapper {
    int deleteByPrimaryKey(String id);

    int insert(TdhTaskParameter record);

    int insertSelective(TdhTaskParameter record);

    TdhTaskParameter selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(TdhTaskParameter record);

    int updateByPrimaryKey(TdhTaskParameter record);

    int updateTdhServiceTaskState(Integer taskState);

    int updateTdhServiceTaskStateMap(Map<String, Object> map);

    int updateTdhServiceTaskTimeByTableName(TdhTaskParameter tdhTaskParameter);

    List<TdhTaskParameter> selectByParameter(TdhTaskParameter tdhTaskParameter);

    int updateTdhServiceTaskStateTo0(List tables);

    int updateByTaskName(TdhTaskParameter tdhTaskParameter);
}