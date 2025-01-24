package com.hula.job.cycle;

import cn.hutool.core.collection.CollUtil;
import com.hula.esdao.QuestionEsDao;
import com.hula.mapper.QuestionMapper;
import com.hula.model.dto.question.QuestionEsDTO;
import com.hula.model.entity.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 增量同步题目到es
 */
@Component
@Slf4j
public class IncSyncQuestionToEs {

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private QuestionEsDao questionEsDao;

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 1000)//这是一个定时任务注解，表示 run 方法会每分钟执行一次。
    public void run() {
        // 查询近 5 分钟内的数据
        long FIVE_MINUTES = 5 * 60 * 1000L;
        Date fiveMinutesAgoDate = new Date(new Date().getTime() - FIVE_MINUTES);//5分钟前的时间
        List<Question> questionList = questionMapper.listQuestionWithDelete(fiveMinutesAgoDate);//查询近5分钟内被删除的题目
        if (CollUtil.isEmpty(questionList)) {
            log.info("no inc question");
            return;
        }
        List<QuestionEsDTO> questionEsDTOList = questionList.stream()
                .map(QuestionEsDTO::objToDto)
                .collect(Collectors.toList());
        final int pageSize = 500;
        int total = questionEsDTOList.size();
        log.info("IncSyncQuestionToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            questionEsDao.saveAll(questionEsDTOList.subList(i, end));//调用saveAll方法将数据批量保存到Elasticsearch中。
        }
        log.info("IncSyncQuestionToEs end, total {}", total);
    }
}
