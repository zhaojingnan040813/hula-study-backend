package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.model.entity.QuestionBank;
import generator.service.QuestionBankService;
import com.hula.mapper.QuestionBankMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【question_bank(题库)】的数据库操作Service实现
* @createDate 2025-01-14 00:38:38
*/
@Service
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank>
    implements QuestionBankService{

}




