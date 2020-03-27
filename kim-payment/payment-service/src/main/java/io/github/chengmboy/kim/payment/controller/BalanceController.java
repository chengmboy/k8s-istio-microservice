package io.github.chengmboy.kim.payment.controller;

import io.github.chengmboy.kim.common.exception.EntityAlreadyExistException;
import io.github.chengmboy.kim.common.util.UserUtils;
import io.github.chengmboy.kim.common.web.Response;
import io.github.chengmboy.kim.payment.api.BalanceRemoteService;
import io.github.chengmboy.kim.payment.api.dto.BalanceDTO;
import io.github.chengmboy.kim.payment.api.exception.OutOfMoneyException;
import io.github.chengmboy.kim.payment.service.BalanceService;
import io.github.chengmboy.kim.uc.api.UcRemoteService;
import io.github.chengmboy.kim.uc.api.dto.UserDTO;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author cheng_mboy
 */
@RestController
@RequiredArgsConstructor
public class BalanceController implements BalanceRemoteService {

    final BalanceService balanceService;
    final UcRemoteService ucRemoteService;

    @Override
    public Response<String> query() {
//        String loginName = jwt.getSubject();
        /*UserDTO user = userRemoteService.getByLoginName(loginName);
        BalanceDTO balance = balanceService.query(user.getId());
        return Response.ok(balance);*/
        String name=UserUtils.getUser();
        UserDTO user = ucRemoteService.getByLoginName(name);
        if (user == null) {
            return Response.ok("Hello anonymous user");
        }
        return Response.ok(String.format("Hello v2, %s!, id: %s email: %s" , user.getName(),user.getId(),user.getEmail()));
        //return Response.ok(String.format("Hello v1, %s!, id: %s " , user.getName(),user.getId()));
    }

    @Override
    public Response<BalanceDTO> recharge(
            @ApiParam(required = true, value = "账户id")
            @RequestParam Long balanceId, @ApiParam(required = true, value = "充值金额") @RequestParam BigDecimal num){
        BalanceDTO balance = balanceService.recharge(balanceId, num);
        return Response.ok(balance);
    }

    @Override
    public Response<BalanceDTO> create(@RequestParam Long userId) throws EntityAlreadyExistException {
        BalanceDTO balance = balanceService.create(userId);
        return Response.ok(balance);
    }

    @Override
    public Response<BalanceDTO> freeze(@RequestParam Long balanceId, @RequestParam BigDecimal num) throws OutOfMoneyException {
        BalanceDTO balance = balanceService.freeze(balanceId, num);
        return Response.ok(balance);
    }

    @Override
    public Response freeze(@RequestParam Long freezeId){
        balanceService.deduct(freezeId);
        return Response.ok();
    }

    @Override
    public Response cancel(@RequestParam Long freezeId){
        balanceService.cancel(freezeId);
        return Response.ok();
    }
}
