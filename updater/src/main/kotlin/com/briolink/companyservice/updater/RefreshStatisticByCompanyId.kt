package com.briolink.companyservice.updater

import java.util.UUID

data class RefreshStatisticByCompanyId(val companyId: UUID, val isUpdateCollaborating: Boolean = false)
