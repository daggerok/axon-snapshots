package daggerok.chat

import org.axonframework.commandhandling.TargetAggregateIdentifier

data class CreateRoomCommand(val roomId: String)
data class RoomCreatedEvent(val roomId: String? = null)

data class EnterRoomCommand(@TargetAggregateIdentifier val roomId: String, val memberId: String)
data class RoomEnteredEvent(val roomId: String? = null, val memberId: String? = null)

data class LeaveRoomCommand(@TargetAggregateIdentifier val roomId: String, val memberId: String)
data class LeftRoomEvent(val roomId: String? = null, val memberId: String? = null)

data class PostMessageCommand(@TargetAggregateIdentifier val roomId: String, val memberId: String, val message: String)
data class MessageSentEvent(val roomId: String? = null, val memberId: String? = null, val message: String? = null)
