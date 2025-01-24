package com.hula.esdao;

import com.hula.model.dto.post.PostEsDTO;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 帖子 ES 操作
 *
 * @author: 赵景南
 *
 */
//复制该接口，PostEsDTO替换成要关联的数据的类型，第二个是索引的id的类型
// 继承了该接口就相当于数据库实体类继承了BaseMapper
public interface PostEsDao extends ElasticsearchRepository<PostEsDTO, Long> {

    List<PostEsDTO> findByUserId(Long userId);//根据方法名自动映射为查询操作
}