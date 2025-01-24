package com.hula.mapper;

import com.hula.model.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
* @author Administrator
* @description 针对表【question(题目)】的数据库操作Mapper
* @createDate 2025-01-14 00:38:38
* @Entity com.hula.model.entity.Question
*/
public interface QuestionMapper extends BaseMapper<Question> {
    //查询已经被删除的数据的方法，我们要把逻辑删除的字段也给同步过去
    //在mybatis中，如果要查询已经被逻辑删除的数据，是没有办法的，因为mybatis会自动帮你加上条件 isDeletd=1
    /**
     * 查询题目列表（包括已被删除的数据）
     */
    @Select("select * from question where updateTime >= #{minUpdateTime}")//这样无论是删除的还是添加的，只要是最近更新的都能查出来
    List<Question> listQuestionWithDelete(Date minUpdateTime);

}




