package com.pinyougou.content.service.impl;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service(timeout=5000)
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);
		
		try {
			//清除缓存 
			redisTemplate.<Long,List<TbContent>>boundHashOps("content").delete(content.getCategoryId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		try {
			Long categoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
			//清除缓存 
			redisTemplate.<Long,List<TbContent>>boundHashOps("content").delete(categoryId);
			if(!content.getCategoryId().equals(categoryId)) {
				redisTemplate.<Long,List<TbContent>>boundHashOps("content").delete(content.getCategoryId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		contentMapper.updateByPrimaryKey(content);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		Set<Long> set = new HashSet<Long>();
		for(Long id:ids){
			Long categoryId = contentMapper.selectByPrimaryKey(id).getCategoryId();
			set.add(categoryId);
			
			contentMapper.deleteByPrimaryKey(id);
		}
		try {
			//清除缓存
			for (Long categoryId : set) {
				redisTemplate.<Long,List<TbContent>>boundHashOps("content").delete(categoryId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		@Override
		public List<TbContent> findByCategoryId(Long categoryId) {
			List<TbContent> list = null;
			
			try {
				list = redisTemplate.<Long, List<TbContent>>boundHashOps("content").get(categoryId);
				if (list != null) {
					return list;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			TbContentExample example = new TbContentExample();
			Criteria criteria = example.createCriteria();
			criteria.andCategoryIdEqualTo(categoryId);
			criteria.andStatusEqualTo("1");//开启状态
			example.setOrderByClause("sort_order");//排序
			list = contentMapper.selectByExample(example);
			
			try {
				redisTemplate.<Long,List<TbContent>>boundHashOps("content").put(categoryId, list);
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			return list;
		}
	
}