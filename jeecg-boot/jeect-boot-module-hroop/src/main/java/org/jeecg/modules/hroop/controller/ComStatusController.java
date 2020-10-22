package org.jeecg.modules.hroop.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.LimitSubmit;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.hroop.entity.ComStatus;
import org.jeecg.modules.hroop.service.IComStatusService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: 更新状态表
 * @Author: jeecg-boot
 * @Date:   2020-10-19
 * @Version: V1.0
 */
@Api(tags="更新状态表")
@RestController
@RequestMapping("/hroop/comStatus")
@Slf4j
public class ComStatusController extends JeecgController<ComStatus, IComStatusService> {
	@Autowired
	private IComStatusService comStatusService;


	 @GetMapping(value = "/hello")
	 @LimitSubmit(key = "testLimit:%s:#orderId",limit = 10,needAllWait = true)
	 public Result<String> hello() {
		 Result<String> result = new Result<String>();
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 result.setResult("Hello World!");
		 result.setMessage(sysUser.getRealname());
		 result.setSuccess(true);
		 return result;
	 }



	/**
	 * 分页列表查询
	 *
	 * @param comStatus
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "更新状态表-分页列表查询")
	@ApiOperation(value="更新状态表-分页列表查询", notes="更新状态表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ComStatus comStatus,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ComStatus> queryWrapper = QueryGenerator.initQueryWrapper(comStatus, req.getParameterMap());
		Page<ComStatus> page = new Page<ComStatus>(pageNo, pageSize);
		IPage<ComStatus> pageList = comStatusService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param comStatus
	 * @return
	 */
	@AutoLog(value = "更新状态表-添加")
	@ApiOperation(value="更新状态表-添加", notes="更新状态表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ComStatus comStatus) {
		comStatusService.save(comStatus);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param comStatus
	 * @return
	 */
	@AutoLog(value = "更新状态表-编辑")
	@ApiOperation(value="更新状态表-编辑", notes="更新状态表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody ComStatus comStatus) {
		comStatusService.updateById(comStatus);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "更新状态表-通过id删除")
	@ApiOperation(value="更新状态表-通过id删除", notes="更新状态表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		comStatusService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "更新状态表-批量删除")
	@ApiOperation(value="更新状态表-批量删除", notes="更新状态表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.comStatusService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "更新状态表-通过id查询")
	@ApiOperation(value="更新状态表-通过id查询", notes="更新状态表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		ComStatus comStatus = comStatusService.getById(id);
		if(comStatus==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(comStatus);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param comStatus
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ComStatus comStatus) {
        return super.exportXls(request, comStatus, ComStatus.class, "更新状态表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ComStatus.class);
    }

}