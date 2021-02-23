package demoparser
package config

case class ParserConfig(ignoredGameEvents: Set[String])

object ParserConfig {
  val Default: ParserConfig = ParserConfig(
    Set(
      "player_footstep",
      "player_jump",
      "item_remove",
      "item_equip",
      "item_pickup",
      "hltv_chase",
      "hltv_status",
      "player_spawn",
      "cs_round_start_beep",
      "bomb_dropped",
      "player_connect_full",
      "buytime_ended",
      "player_connect",
      "player_falldamage",
      "bomb_defused",
      "round_announce_match_start",
      "bomb_beginplant",
      "bomb_begindefuse",
      "bomb_exploded",
      "begin_new_match",
      "round_time_warning",
      "player_disconnect",
      "bomb_planted",
      "bomb_beginplant",
      "cs_win_panel_round",
      "round_end",
      "round_officially_ended",
      "cs_round_final_beep",
      "round_freeze_end",
      "round_freeze_end",
      "player_team",
      "round_start",
      "round_prestart",
      "cs_pre_restart",
      "round_poststart",
      "other_death",
      "buytime_ended"
    )
  )
}
