package io.github.chengmboy.kim.payment.api;

import io.github.chengmboy.kim.common.exception.EntityAlreadyExistException;
import io.github.chengmboy.kim.common.web.Response;
import io.github.chengmboy.kim.payment.api.constants.PaymentConstants;
import io.github.chengmboy.kim.payment.api.dto.BalanceDTO;
import io.github.chengmboy.kim.payment.api.exception.OutOfMoneyException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * @author cheng_mboy
 */
@FeignClient(PaymentConstants.KimPayment)
@Api(description = "账户服务")
public interface BalanceRemoteService {

    @ApiOperation("查询账户")
    @GetMapping("/balance/query")
    Response<String> query();


    @ApiOperation("充值金额")
    @PutMapping("/balance/recharge")
    Response<BalanceDTO> recharge(
            @ApiParam(required = true, value = "账户id")
            @RequestParam Long balanceId, @ApiParam(required = true, value = "充值金额") @RequestParam BigDecimal num);

    @ApiOperation("创建账户")
    @PostMapping("/balance/create")
    Response<BalanceDTO> create(@RequestParam Long userId) throws EntityAlreadyExistException;

    @ApiOperation("冻结账户金额")
    @PostMapping("/balance/freeze")
    Response<BalanceDTO> freeze(@RequestParam Long balanceId, @RequestParam BigDecimal num) throws OutOfMoneyException;

    @ApiOperation("消费冻结金额")
    @DeleteMapping("/balance/deduct")
    Response freeze(@RequestParam Long freezeId);

    @ApiOperation("取消冻结金额")
    @DeleteMapping("/balance/cancel")
    Response cancel(@RequestParam Long freezeId);
}
