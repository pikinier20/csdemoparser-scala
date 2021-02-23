package demoparser
package parser.handlers

import scalapb.GeneratedMessage

import demoparser.parser.model.{Message, NETMessage, SVCMessage}
import netmessages._

object MessageHandler {
  def apply(msg: Message[_]): (Array[Byte] => GeneratedMessage) =
    msg match {
      case SVCMessage(msg) => this(msg)
      case NETMessage(msg) => this(msg)
    }
  def apply(msg: NET_Messages): (Array[Byte] => GeneratedMessage) =
    (array: Array[Byte]) =>
      (msg match {
        case NET_Messages.net_NOP              => CNETMsg_NOP
        case NET_Messages.net_Disconnect       => CNETMsg_Disconnect
        case NET_Messages.net_File             => CNETMsg_File
        case NET_Messages.net_SplitScreenUser  => CNETMsg_SplitScreenUser
        case NET_Messages.net_Tick             => CNETMsg_Tick
        case NET_Messages.net_StringCmd        => CNETMsg_StringCmd
        case NET_Messages.net_SetConVar        => CNETMsg_SetConVar
        case NET_Messages.net_SignonState      => CNETMsg_SignonState
        case NET_Messages.net_PlayerAvatarData => CNETMsg_PlayerAvatarData
        case NET_Messages.Unrecognized(unrecognizedValue) =>
          throw new IllegalStateException(
            s"Unrecognized message found $unrecognizedValue"
          )
      }).parseFrom(array)

  def apply(msg: SVC_Messages): (Array[Byte] => GeneratedMessage) =
    (array: Array[Byte]) =>
      (msg match {
        case SVC_Messages.svc_ServerInfo        => CSVCMsg_ServerInfo
        case SVC_Messages.svc_SendTable         => CSVCMsg_SendTable
        case SVC_Messages.svc_ClassInfo         => CSVCMsg_ClassInfo
        case SVC_Messages.svc_SetPause          => CSVCMsg_SetPause
        case SVC_Messages.svc_CreateStringTable => CSVCMsg_CreateStringTable
        case SVC_Messages.svc_UpdateStringTable => CSVCMsg_UpdateStringTable
        case SVC_Messages.svc_VoiceInit         => CSVCMsg_VoiceInit
        case SVC_Messages.svc_VoiceData         => CSVCMsg_VoiceData
        case SVC_Messages.svc_Print             => CSVCMsg_Print
        case SVC_Messages.svc_Sounds            => CSVCMsg_Sounds
        case SVC_Messages.svc_SetView           => CSVCMsg_SetView
        case SVC_Messages.svc_FixAngle          => CSVCMsg_FixAngle
        case SVC_Messages.svc_CrosshairAngle    => CSVCMsg_CrosshairAngle
        case SVC_Messages.svc_BSPDecal          => CSVCMsg_BSPDecal
        case SVC_Messages.svc_SplitScreen       => CSVCMsg_SplitScreen
        case SVC_Messages.svc_UserMessage       => CSVCMsg_UserMessage
        case SVC_Messages.svc_EntityMessage     => CSVCMsg_EntityMsg
        case SVC_Messages.svc_GameEvent         => CSVCMsg_GameEvent
        case SVC_Messages.svc_PacketEntities    => CSVCMsg_PacketEntities
        case SVC_Messages.svc_TempEntities      => CSVCMsg_TempEntities
        case SVC_Messages.svc_Prefetch          => CSVCMsg_Prefetch
        case SVC_Messages.svc_Menu              => CSVCMsg_Menu
        case SVC_Messages.svc_GameEventList     => CSVCMsg_GameEventList
        case SVC_Messages.svc_GetCvarValue      => CSVCMsg_GetCvarValue
        case SVC_Messages.svc_PaintmapData      => CSVCMsg_PaintmapData
        case SVC_Messages.svc_CmdKeyValues      => CSVCMsg_CmdKeyValues
        case SVC_Messages.svc_EncryptedData     => CSVCMsg_EncryptedData
        case SVC_Messages.svc_HltvReplay        => CSVCMsg_HltvReplay
        case SVC_Messages.svc_Broadcast_Command => CSVCMsg_Broadcast_Command
        case SVC_Messages.Unrecognized(unrecognizedValue) =>
          throw new IllegalStateException(
            s"Unrecognized message found $unrecognizedValue"
          )
      }).parseFrom(array)
}
