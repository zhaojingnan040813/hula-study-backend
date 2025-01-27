package com.hula.model.dto.questionbankquestion;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量删除题目中的题库
 */
@Data
public class QuestionBankQuestionBatchRemoveRequest implements Serializable {

    /**
     * 题库 id
     */
    private Long questionBankId;

    /**
     * 题目 id 列表
     */
    private List<Long> questionIdList;

    private static final long serialVersionUID = 1L;
}
/**
 * 它和 QuestionBankQuestionBatchAddRequest 是一样的，但是我们为了见名知意，还是选择自己再定义了一个类
 */
