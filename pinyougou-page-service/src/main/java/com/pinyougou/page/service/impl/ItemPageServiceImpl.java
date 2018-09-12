package com.pinyougou.page.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;

import freemarker.template.Configuration;
import freemarker.template.Template;


@Service
public class ItemPageServiceImpl implements ItemPageService{
	
	@Autowired
	private FreeMarkerConfig freemarkerConfig;
	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Value("${pagedir}")
	private String pagedir;
	
	@Override
	public boolean genItemHtml(Long goodsId) {
		Writer out = null;
		try {
			Configuration configuration = freemarkerConfig.getConfiguration();
			Template template = configuration.getTemplate("item.ftl");

			Map<String, Object> dataModel = new HashMap<String, Object>();
			TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
			dataModel.put("goods", goods);
			dataModel.put("goodsDesc", goodsDescMapper.selectByPrimaryKey(goodsId));
			dataModel.put("itemCat1", itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName());
			dataModel.put("itemCat2", itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName());
			dataModel.put("itemCat3", itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName());
			
			//SKU列表
			TbItemExample example = new TbItemExample();
			Criteria criteria = example.createCriteria();
			criteria.andGoodsIdEqualTo(goodsId);
			criteria.andStatusEqualTo("1");//状态为有效
			example.setOrderByClause("is_default desc");
			List<TbItem> itemList = itemMapper.selectByExample(example);
			dataModel.put("itemList", itemList);
			
			

			out = new OutputStreamWriter(new FileOutputStream(pagedir + goodsId + ".html"),"UTF-8");
			template.process(dataModel, out);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					//ignore
				}
				out = null;
			}
		}
		return false;
	}

	@Override
	public boolean deleteItemHtml(Long[] goodsIds) {
		try {
			for (Long goodsId : goodsIds) {
				File file = new File(pagedir + goodsId + ".html");
				if(file.exists()){
					file.delete();
					System.out.println("delete"+goodsId);
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
