package com.hula.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.common.ErrorCode;
import com.hula.constant.CommonConstant;
import com.hula.exception.BusinessException;
import com.hula.exception.ThrowUtils;
import com.hula.mapper.QuestionBankQuestionMapper;
import com.hula.model.dto.questionbankquestion.QuestionBankQuestionQueryRequest;
import com.hula.model.entity.Question;
import com.hula.model.entity.QuestionBank;
import com.hula.model.entity.QuestionBankQuestion;
import com.hula.model.entity.User;
import com.hula.model.vo.QuestionBankQuestionVO;
import com.hula.model.vo.UserVO;
import com.hula.service.QuestionBankQuestionService;
import com.hula.service.QuestionService;
import com.hula.service.UserService;
import com.hula.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 题目题库关联表服务实现
 *
 * @author: 赵景南
 */
@Service
@Slf4j
public class QuestionBankQuestionServiceImpl extends ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion> implements QuestionBankQuestionService {

    @Resource
    private UserService userService;

    @Lazy // 解决循环依赖问题
    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionBankServiceImpl questionBankService;


    /**
     * 校验数据
     *
     * @param questionBankQuestion
     * @param add                  对创建的数据进行校验
     */
    @Override
    public void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion, boolean add) {
        ThrowUtils.throwIf(questionBankQuestion == null, ErrorCode.PARAMS_ERROR);
        // 题目和题库必须存在  这个方法是会给多个 Controller 复用的
        Long questionId = questionBankQuestion.getQuestionId();
        if (questionId != null) {
            Question question = questionService.getById(questionId);
            ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        Long questionBankId = questionBankQuestion.getQuestionBankId();
        if (questionBankId != null) {
            QuestionBank questionBank = questionBankService.getById(questionBankId);
            ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR, "题库不存在");
        }
        // todo 从对象中取值
//        String title = questionBankQuestion.getTitle();
        // 创建数据时，参数不能为空
//        if (add) {
//            // todo 补充校验规则
//            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
//        }
//        // 修改数据时，有参数则校验
//        // todo 补充校验规则
//        if (StringUtils.isNotBlank(title)) {
//            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
//        }


    }

    /**
     * 获取查询条件
     *
     * @param questionBankQuestionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest) {
        QueryWrapper<QuestionBankQuestion> queryWrapper = new QueryWrapper<>();
        if (questionBankQuestionQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = questionBankQuestionQueryRequest.getId();
        Long notId = questionBankQuestionQueryRequest.getNotId();
        String sortField = questionBankQuestionQueryRequest.getSortField();
        String sortOrder = questionBankQuestionQueryRequest.getSortOrder();
        Long userId = questionBankQuestionQueryRequest.getUserId();
        Long questionId = questionBankQuestionQueryRequest.getQuestionId();
        Long questionBankId = questionBankQuestionQueryRequest.getQuestionBankId();
        // todo 补充需要的查询条件
        // 从多字段中搜索
//        if (StringUtils.isNotBlank(searchText)) {
//            // 需要拼接查询条件
//            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
//        }
//        // 模糊查询
//        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
//        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
//        // JSON 数组查询
//        if (CollUtil.isNotEmpty(tagList)) {
//            for (String tag : tagList) {
//                queryWrapper.like("tags", "\"" + tag + "\"");
//            }
//        }
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionBankId), "questionBankId", questionBankId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题目题库关联表封装
     *
     * @param questionBankQuestion
     * @param request
     * @return
     */
    @Override
    public QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion, HttpServletRequest request) {
        // 对象转封装类
        QuestionBankQuestionVO questionBankQuestionVO = QuestionBankQuestionVO.objToVo(questionBankQuestion);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = questionBankQuestion.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionBankQuestionVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        long questionBankQuestionId = questionBankQuestion.getId();
        User loginUser = userService.getLoginUserPermitNull(request);
//        if (loginUser != null) {
//            // 获取点赞
//            QueryWrapper<QuestionBankQuestionThumb> questionBankQuestionThumbQueryWrapper = new QueryWrapper<>();
//            questionBankQuestionThumbQueryWrapper.in("questionBankQuestionId", questionBankQuestionId);
//            questionBankQuestionThumbQueryWrapper.eq("userId", loginUser.getId());
//            QuestionBankQuestionThumb questionBankQuestionThumb = questionBankQuestionThumbMapper.selectOne(questionBankQuestionThumbQueryWrapper);
//            questionBankQuestionVO.setHasThumb(questionBankQuestionThumb != null);
//            // 获取收藏
//            QueryWrapper<QuestionBankQuestionFavour> questionBankQuestionFavourQueryWrapper = new QueryWrapper<>();
//            questionBankQuestionFavourQueryWrapper.in("questionBankQuestionId", questionBankQuestionId);
//            questionBankQuestionFavourQueryWrapper.eq("userId", loginUser.getId());
//            QuestionBankQuestionFavour questionBankQuestionFavour = questionBankQuestionFavourMapper.selectOne(questionBankQuestionFavourQueryWrapper);
//            questionBankQuestionVO.setHasFavour(questionBankQuestionFavour != null);
//        }
        // endregion

        return questionBankQuestionVO;
    }

    /**
     * 分页获取题库题目关联封装
     *
     * @param questionBankQuestionPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> questionBankQuestionPage, HttpServletRequest request) {
        List<QuestionBankQuestion> questionBankQuestionList = questionBankQuestionPage.getRecords();
        Page<QuestionBankQuestionVO> questionBankQuestionVOPage = new Page<>(questionBankQuestionPage.getCurrent(), questionBankQuestionPage.getSize(), questionBankQuestionPage.getTotal());
        if (CollUtil.isEmpty(questionBankQuestionList)) {
            return questionBankQuestionVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionBankQuestionVO> questionBankQuestionVOList = questionBankQuestionList.stream().map(questionBankQuestion -> {
            return QuestionBankQuestionVO.objToVo(questionBankQuestion);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionBankQuestionList.stream().map(QuestionBankQuestion::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        questionBankQuestionVOList.forEach(questionBankQuestionVO -> {
            Long userId = questionBankQuestionVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionBankQuestionVO.setUser(userService.getUserVO(user));
        });
        // endregion

        questionBankQuestionVOPage.setRecords(questionBankQuestionVOList);
        return questionBankQuestionVOPage;
    }



    /**
     * 批量添加题目到题库
     *
     * @param questionIdList
     * @param questionBankId
     * @param loginUser
     */
    @Override
    public void batchAddQuestionsToBank(List<Long> questionIdList, long questionBankId, User loginUser) {
        // 参数校验
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR, "题目列表不能为空");
        ThrowUtils.throwIf(questionBankId <= 0, ErrorCode.PARAMS_ERROR, "题库 id 非法");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 检查题目 id 是否存在   SELECT id FROM question WHERE id IN (/* questionIdList 中的值 */)
        LambdaQueryWrapper<Question> questionLambdaQueryWrapper = Wrappers.lambdaQuery(Question.class)
                .select(Question::getId)
                .in(Question::getId, questionIdList);
        // 合法的题目 id 列表  这样就不是select * ，查询的效率就会高一些
        List<Long> validQuestionIdList = questionService.listObjs(questionLambdaQueryWrapper, obj -> (Long) obj);
        ThrowUtils.throwIf(CollUtil.isEmpty(validQuestionIdList), ErrorCode.PARAMS_ERROR, "合法的题目 id 列表为空");
        // 检查哪些题目还不存在于题库中，避免重复插入
        LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                .eq(QuestionBankQuestion::getQuestionBankId, questionBankId)
                .in(QuestionBankQuestion::getQuestionId, validQuestionIdList);
        List<QuestionBankQuestion> existQuestionList = this.list(lambdaQueryWrapper);//这里调用的是list方法
        // 已存在于题库中的题目 id
        Set<Long> existQuestionIdSet = existQuestionList.stream()
                .map(QuestionBankQuestion::getId)
                .collect(Collectors.toSet());
        // 已存在于题库中的题目 id，不需要再次添加（这里有一点难懂）
        validQuestionIdList = validQuestionIdList.stream().filter(questionId -> {
            return !existQuestionIdSet.contains(questionId);
        }).collect(Collectors.toList());
        ThrowUtils.throwIf(CollUtil.isEmpty(validQuestionIdList), ErrorCode.PARAMS_ERROR, "所有题目都已存在于题库中");
        // 检查题库 id 是否存在
        QuestionBank questionBank = questionBankService.getById(questionBankId);
        ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR, "题库不存在");

        // 自定义线程池（IO 密集型线程池）
        ThreadPoolExecutor customExecutor = new ThreadPoolExecutor(
                20,             // 核心线程数
                50,                        // 最大线程数
                60L,                       // 线程空闲存活时间
                TimeUnit.SECONDS,           // 存活时间单位
                new LinkedBlockingQueue<>(10000),  // 阻塞队列容量
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略：由调用线程处理任务
        );

        // 保存所有批次任务
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        // 分批处理，避免长事务，假设每次处理 1000 条数据
        int batchSize = 1000;
        int totalQuestionListSize = validQuestionIdList.size();
        for (int i = 0; i < totalQuestionListSize; i += batchSize) {
            // 生成每批次的数据
            List<Long> subList = validQuestionIdList.subList(i, Math.min(i + batchSize, totalQuestionListSize));
            //stream().map() 方法用于将流中的每个元素映射为另一个对象
            List<QuestionBankQuestion> questionBankQuestions = subList.stream()
                    .map(questionId -> {
                        QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
                        questionBankQuestion.setQuestionBankId(questionBankId);
                        questionBankQuestion.setQuestionId(questionId);
                        questionBankQuestion.setUserId(loginUser.getId());
                        return questionBankQuestion;
                    }).collect(Collectors.toList());
            // 使用事务处理每批数据
            // 获取代理 不能使用 this 不然事务会失效
            QuestionBankQuestionService questionBankQuestionService = (QuestionBankQuestionServiceImpl) AopContext.currentProxy();

            // 异步处理每批数据，将任务添加到异步任务列表
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                questionBankQuestionService.batchAddQuestionsToBankInner(questionBankQuestions);
            }, customExecutor).exceptionally(ex->{
                log.error("批量添加题目到题库失败", ex);
                return null;
            });
            futures.add(future);
        }
        // 等待所有批次完成操作
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        // 关闭线程池
        customExecutor.shutdown();
    }
    /**
     * 批量添加题目到题库（事务，仅供内部调用）
     *
     * @param questionBankQuestions
     */
    @Async
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAddQuestionsToBankInner(List<QuestionBankQuestion> questionBankQuestions) {
        try {
            boolean result = this.saveBatch(questionBankQuestions);// 这一段代码是核心(使用了mybatisplus的批量插入) 对一批操作进行事务管理：
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "向题库添加题目失败");
        } catch (DataIntegrityViolationException e) {
            log.error("数据库唯一键冲突或违反其他完整性约束, 错误信息: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目已存在于该题库，无法重复添加");
        } catch (DataAccessException e) {
            log.error("数据库连接问题、事务问题等导致操作失败, 错误信息: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库操作失败");
        } catch (Exception e) {
            // 捕获其他异常，做通用处理
            log.error("添加题目到题库时发生未知错误，错误信息: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "向题库添加题目失败");
        }
    }


    /**
     * 批量从题库中移除题目
     * 注意：要记录一下是谁移除的（但是这里却，没有）
     * @param questionIdList
     * @param questionBankId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchRemoveQuestionsFromBank(List<Long> questionIdList, Long questionBankId) {
        // 参数校验
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR, "题目列表为空");
        ThrowUtils.throwIf(questionBankId == null || questionBankId <= 0, ErrorCode.PARAMS_ERROR, "题库非法");
        // 执行删除关联，不需要再从数据库中查这些题目是否存在了
        for (Long questionId : questionIdList) {
            // 构造查询
            LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                    .eq(QuestionBankQuestion::getQuestionId, questionId)
                    .eq(QuestionBankQuestion::getQuestionBankId, questionBankId);//构造单个的删除条件
            boolean result = this.remove(lambdaQueryWrapper);
            if (!result) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "从题库移除题目失败");
            }
        }
    }



}
