package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbItem;

public interface ItemSearchService {
	
	public Map<String,Object> search(Map<String,Object> searchMap);

	public void importList(List<TbItem> list);
	
	public void deleteByGoodsIds(List goodsIdList);
}
