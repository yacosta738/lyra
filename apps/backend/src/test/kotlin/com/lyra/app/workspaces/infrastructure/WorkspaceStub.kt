package com.lyra.app.workspaces.infrastructure

import com.lyra.app.workspaces.domain.Workspace
import com.lyra.app.workspaces.domain.WorkspaceCollaborators
import com.lyra.app.workspaces.domain.WorkspaceRole
import com.lyra.app.workspaces.infrastructure.http.request.CreateWorkspaceRequest
import java.util.*
import net.datafaker.Faker

object WorkspaceStub {
    private val faker = Faker()
    fun create(
        id: UUID = UUID.randomUUID(),
        name: String = faker.lorem().words(3).joinToString(" "),
        userId: UUID = UUID.randomUUID()
    ): Workspace = Workspace.create(
        id = id,
        name = name,
        userId = userId,
    )

    fun createCollaborator(
        workspaceId: UUID = UUID.randomUUID(),
        userId: UUID = UUID.randomUUID(),
        role: WorkspaceRole = WorkspaceRole.COLLABORATOR,
    ): WorkspaceCollaborators = WorkspaceCollaborators.create(
        workspaceId = workspaceId.toString(),
        userId = userId.toString(),
        role = role,
    )

    fun generateRequest(
        name: String = faker.lorem().words(3).joinToString(" "),
        userId: String = UUID.randomUUID().toString(),
    ): CreateWorkspaceRequest = CreateWorkspaceRequest(
        name = name,
        userId = userId,
    )
}
