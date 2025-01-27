package com.hula.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hula.model.dto.questionbankquestion.QuestionBankQuestionQueryRequest;
import com.hula.model.entity.QuestionBankQuestion;
import com.hula.model.entity.User;
import com.hula.model.vo.QuestionBankQuestionVO;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题目题库关联表服务
 *
 * @author: 赵景南
 */
public interface QuestionBankQuestionService extends IService<QuestionBankQuestion> {

    /**
     * 校验数据
     *
     * @param questionBankQuestion
     * @param add                  对创建的数据进行校验
     */
    void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionBankQuestionQueryRequest
     * @return
     */
    QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest);

    /**
     * 获取题目题库关联表封装
     *
     * @param questionBankQuestion
     * @param request
     * @return
     */
    QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion, HttpServletRequest request);

    /**
     * 分页获取题目题库关联表封装
     *
     * @param questionBankQuestionPage
     * @param request
     * @return
     */
    Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> questionBankQuestionPage, HttpServletRequest request);


    /**
     * 批量添加题目到题库
     * 注意：要记录一下是谁添加的
     * 返回值应该是什么呢？
     */
    void batchAddQuestionsToBank(List<Long> questionsIdList, Long questionBankId, User loginUser);


    @Transactional(rollbackFor = Exception.class)
    void batchRemoveQuestionsFromBank(List<Long> questionIdList, Long questionBankId);
}
