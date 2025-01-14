package com.hula.model.dto.questionbankquestion;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新题目题库关联表请求
 *
 * @author: 赵景南
 *  
 */
@Data
public class QuestionBankQuestionUpdateRequest implements Serializable {

    /**
     * id  这个字段应该是更新关联的时候用的吧，
     * 还是给管理员留一个这个入口吧，说不定有这个需求呢
     */
    private Long id;

    /**
     * 题库 id
     */
    private Long questionBankId;

    /**
     * 题目 id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
}