package com.hula.job.once;

import cn.hutool.core.collection.CollUtil;
import com.hula.esdao.PostEsDao;
import com.hula.esdao.QuestionEsDao;
import com.hula.model.dto.post.PostEsDTO;
import com.hula.model.dto.question.QuestionEsDTO;
import com.hula.model.entity.Post;
import com.hula.model.entity.Question;
import com.hula.service.PostService;
import com.hula.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全量同步题目到 es
 *
 * @author: 赵景南
 * 只要实现了 CommandLineRunner 接口，SpringBoot 应用启动时会自动执行 run 方法
 * 以后如果你不想要在项目启动的时候加载这个任务，那就把 @Component 注解去掉即可
 *
 */
@Component
@Slf4j
public class FullSyncQuestionToEs implements CommandLineRunner {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionEsDao questionEsDao;

    @Override
    public void run(String... args) {
        // 全量获取题目（数据量不大的情况下使用）
        List<Question> questionList = questionService.list();//先从数据库里面全量获取题目
        if (CollUtil.isEmpty(questionList)) {
            return;
        }
        // 转为 ES 实体类
        List<QuestionEsDTO> questionEsDTOList = questionList.stream()
                .map(QuestionEsDTO::objToDto)
                .collect(Collectors.toList());
        // 分页批量插入到 ES 之所以分页，是为了防止把es搞挂了，因为 es 默认的插入限制是1000条，写入的性能要比查询慢很多
        final int pageSize = 500;
        int total = questionEsDTOList.size();
        log.info("FullSyncQuestionToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            // 注意同步的数据下标不能超过总数据量
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            questionEsDao.saveAll(questionEsDTOList.subList(i, end));//分批量保存
        }
        log.info("FullSyncQuestionToEs end, total {}", total);
    }
}

