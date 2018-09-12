package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightEntry.Highlight;
import org.springframework.data.solr.core.query.result.HighlightPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Service(timeout=5000)
public class ItemSearchServiceImpl implements ItemSearchService {
	
	@Autowired
	private SolrTemplate solrTemplate;
	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public Map<String, Object> search(Map<String, Object> searchMap) {
		String keywords = searchMap.get("keywords").toString();
		searchMap.put("keywords", keywords.replaceAll(" ", ""));
		
		
		Map<String,Object> map = new HashMap<String, Object>();
		
		map.putAll(searchList(searchMap));
		
		List<String> categoryList = searchCategoryList(searchMap);
		map.put("categoryList", categoryList);
		
		if(!"".equals(searchMap.get("category"))){
			map.putAll(searchBrandAndSpecList(searchMap.get("category").toString()));
		}else{
			if(categoryList.size()>0){
				map.putAll(searchBrandAndSpecList(categoryList.get(0)));
			}
		}
		
		return map;
	}
	
	/**
	 * 缓存中取出品牌列表和规格列表
	 * @param category
	 * @return
	 */
	private Map searchBrandAndSpecList(String category) {
		Map map = new HashMap();
		
		Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
		if(typeId!=null){
			List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
			map.put("brandList", brandList);
			
			List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
			map.put("specList", specList);
			
		}
		
		
		
		return map;
	}

	/**
	 * 查询分类列表  
	 * @param searchMap
	 * @return
	 */
	private List<String> searchCategoryList(Map<String, Object> searchMap){
		List<String> list = new ArrayList<String>();
		
		Query query = new SimpleQuery();
		
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions);
		
		GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
		GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
		
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		List<GroupEntry<TbItem>> content = groupEntries.getContent();
		for (GroupEntry<TbItem> groupEntry : content) {
			list.add(groupEntry.getGroupValue());
		}
		
		return list;
	}
	
	/**
	 * 根据关键字搜索列表
	 * @param searchMap
	 * @return
	 */
	private Map<String, Object> searchList(Map<String, Object> searchMap) {
		HighlightQuery query = new SimpleHighlightQuery();
		
		//添加高亮设置
		HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
		highlightOptions.setSimplePrefix("<em style='color:red'>");
		highlightOptions.setSimplePostfix("</em>");
		query.setHighlightOptions(highlightOptions);
		
		//查询关键字
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		
		//按分类筛选
		if(!"".equals(searchMap.get("category"))){
			Criteria filtercriteria = new Criteria("item_category").is(searchMap.get("category"));
			FilterQuery filterQuery = new SimpleFilterQuery(filtercriteria );
			query.addFilterQuery(filterQuery);
		}
		
		// 按品牌筛选
		if (!"".equals(searchMap.get("brand"))) {
			Criteria filtercriteria = new Criteria("item_brand").is(searchMap.get("brand"));
			FilterQuery filterQuery = new SimpleFilterQuery(filtercriteria);
			query.addFilterQuery(filterQuery);
		}
		
		// 按规格筛选
		if (searchMap.get("spec")!=null) {
			Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
			for (String key : specMap.keySet()) {
				Criteria filtercriteria = new Criteria("item_spec_"+key).is(specMap.get(key));
				FilterQuery filterQuery = new SimpleFilterQuery(filtercriteria);
				query.addFilterQuery(filterQuery);
			}
		}
		
		// 按价格区间筛选
		if(!"".equals(searchMap.get("price"))){
			String[] price = searchMap.get("price").toString().split("-");
			if(!"0".equals(price[0])){
				Criteria filtercriteria = new Criteria("item_price").greaterThan(price[0]);
				FilterQuery filterQuery = new SimpleFilterQuery(filtercriteria);
				query.addFilterQuery(filterQuery);
			}
			if(!"*".equals(price[1])){
				Criteria filtercriteria = new Criteria("item_price").lessThan(price[1]);
				FilterQuery filterQuery = new SimpleFilterQuery(filtercriteria);
				query.addFilterQuery(filterQuery);
			}
		}
		
		//分页选项
		Integer pageNo,pageSize;
		if(searchMap.get("pageNo")!=null){
			pageNo= Integer.valueOf( searchMap.get("pageNo").toString());
		}else{
			pageNo=1;
		}
		if(searchMap.get("pageSize")!=null){
			pageSize= Integer.valueOf( searchMap.get("pageSize").toString());
		}else{
			pageSize=20;
		}
		query.setOffset((pageNo-1)*pageSize);
		query.setRows(pageSize);
		
		//排序
		if(searchMap.get("sortValue")!=null&&searchMap.get("sortField")!=null){
			String sortValue = searchMap.get("sortValue").toString();
			String sortField = searchMap.get("sortField").toString();
			if(sortValue!=null&&!"".equals(sortValue)){
				Direction direction;
				if("DESC".equals(sortValue)){
					direction = Sort.Direction.DESC;
				}else{
					direction = Sort.Direction.ASC;
				}
				Sort sort = new Sort(direction,"item_"+sortField);
				query.addSort(sort);
			}
		}
		
		
		
		
		
		//执行查询
		HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);
		
		//高亮处理
		List<HighlightEntry<TbItem>> highlighted = highlightPage.getHighlighted();
		for (HighlightEntry<TbItem> highlightEntry : highlighted) {
			TbItem tbItem = highlightEntry.getEntity();
			List<Highlight> highlights = highlightEntry.getHighlights();
			if(highlights.size()>0&&highlights.get(0).getSnipplets().size()>0){
				tbItem.setTitle(highlights.get(0).getSnipplets().get(0));
			}
		}
		
		//封装到map中
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("rows", highlightPage.getContent());
		map.put("totalPages", highlightPage.getTotalPages());//返回总页数
		map.put("total", highlightPage.getTotalElements());//返回总记录数
		
		return map;
	}

	@Override
	public void importList(List<TbItem> list) {
		for (TbItem item : list) {
			Map<String, String> specMap = JSON.parseObject(item.getSpec(),Map.class);
			item.setSpecMap(specMap);
		}
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
	}

	@Override
	public void deleteByGoodsIds(List goodsIdList) {
		SolrDataQuery query = new SimpleQuery("*:*");
		Criteria Criteria = new Criteria("item_goodsid").in(goodsIdList);
		query.addCriteria(Criteria);
		solrTemplate.delete(query);
		solrTemplate.commit();
	}

}
