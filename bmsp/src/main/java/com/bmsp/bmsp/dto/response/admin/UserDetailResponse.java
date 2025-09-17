package com.bmsp.bmsp.dto.response.admin;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class UserDetailResponse {
    private UserManagementResponse user;
    private List<AccountSummary> accounts;
    private List<LoanSummary> loans;
}