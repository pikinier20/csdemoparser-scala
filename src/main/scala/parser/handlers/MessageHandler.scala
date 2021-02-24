package demoparser
package parser.handlers

import scalapb.GeneratedMessage

import demoparser.parser.model.{Message, NETMessage, SVCMessage}
import netmessages._

object MessageHandler {
  def apply(
      msg: Message[_]
  ): (Array[Byte]) => Either[String, GeneratedMessage] =
    msg match {
      case SVCMessage(msg) => this(msg)
      case NETMessage(msg) => this(msg)
    }
  def apply(
      msg: NET_Messages
  ): (Array[Byte]) => Either[String, GeneratedMessage] =
    (array: Array[Byte]) =>
      (msg match {
        case NET_Messages.net_NOP             => Right(CNETMsg_NOP)
        case NET_Messages.net_Disconnect      => Right(CNETMsg_Disconnect)
        case NET_Messages.net_File            => Right(CNETMsg_File)
        case NET_Messages.net_SplitScreenUser => Right(CNETMsg_SplitScreenUser)
        case NET_Messages.net_Tick            => Right(CNETMsg_Tick)
        case NET_Messages.net_StringCmd       => Right(CNETMsg_StringCmd)
        case NET_Messages.net_SetConVar       => Right(CNETMsg_SetConVar)
        case NET_Messages.net_SignonState     => Right(CNETMsg_SignonState)
        case NET_Messages.net_PlayerAvatarData =>
          Right(CNETMsg_PlayerAvatarData)
        case NET_Messages.Unrecognized(unrecognizedValue) =>
          Left("Unrecognized NET_Message $unrecognizedValue")
      }).map(_.parseFrom(array))

  def apply(
      msg: SVC_Messages
  ): (Array[Byte]) => Either[String, GeneratedMessage] =
    (array: Array[Byte]) =>
      (msg match {
        case SVC_Messages.svc_ServerInfo => Right(CSVCMsg_ServerInfo)
        case SVC_Messages.svc_SendTable  => Right(CSVCMsg_SendTable)
        case SVC_Messages.svc_ClassInfo  => Right(CSVCMsg_ClassInfo)
        case SVC_Messages.svc_SetPause   => Right(CSVCMsg_SetPause)
        case SVC_Messages.svc_CreateStringTable =>
          Right(CSVCMsg_CreateStringTable)
        case SVC_Messages.svc_UpdateStringTable =>
          Right(CSVCMsg_UpdateStringTable)
        case SVC_Messages.svc_VoiceInit      => Right(CSVCMsg_VoiceInit)
        case SVC_Messages.svc_VoiceData      => Right(CSVCMsg_VoiceData)
        case SVC_Messages.svc_Print          => Right(CSVCMsg_Print)
        case SVC_Messages.svc_Sounds         => Right(CSVCMsg_Sounds)
        case SVC_Messages.svc_SetView        => Right(CSVCMsg_SetView)
        case SVC_Messages.svc_FixAngle       => Right(CSVCMsg_FixAngle)
        case SVC_Messages.svc_CrosshairAngle => Right(CSVCMsg_CrosshairAngle)
        case SVC_Messages.svc_BSPDecal       => Right(CSVCMsg_BSPDecal)
        case SVC_Messages.svc_SplitScreen    => Right(CSVCMsg_SplitScreen)
        case SVC_Messages.svc_UserMessage    => Right(CSVCMsg_UserMessage)
        case SVC_Messages.svc_EntityMessage  => Right(CSVCMsg_EntityMsg)
        case SVC_Messages.svc_GameEvent      => Right(CSVCMsg_GameEvent)
        case SVC_Messages.svc_PacketEntities => Right(CSVCMsg_PacketEntities)
        case SVC_Messages.svc_TempEntities   => Right(CSVCMsg_TempEntities)
        case SVC_Messages.svc_Prefetch       => Right(CSVCMsg_Prefetch)
        case SVC_Messages.svc_Menu           => Right(CSVCMsg_Menu)
        case SVC_Messages.svc_GameEventList  => Right(CSVCMsg_GameEventList)
        case SVC_Messages.svc_GetCvarValue   => Right(CSVCMsg_GetCvarValue)
        case SVC_Messages.svc_PaintmapData   => Right(CSVCMsg_PaintmapData)
        case SVC_Messages.svc_CmdKeyValues   => Right(CSVCMsg_CmdKeyValues)
        case SVC_Messages.svc_EncryptedData  => Right(CSVCMsg_EncryptedData)
        case SVC_Messages.svc_HltvReplay     => Right(CSVCMsg_HltvReplay)
        case SVC_Messages.svc_Broadcast_Command =>
          Right(CSVCMsg_Broadcast_Command)
        case SVC_Messages.Unrecognized(unrecognizedValue) =>
          Left(s"Unrecognized SVC_Message $unrecognizedValue")
      }).map(_.parseFrom(array))
}
