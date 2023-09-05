/*
 * Copyright (c) 2016-2017, Cameron Moberg <Moberg@tuta.io>
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.bosstilerespawntimer;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import net.runelite.api.Constants;
import net.runelite.api.NpcID;
import net.runelite.client.util.RSTimeUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Map;

enum Boss
{
	GENERAL_GRAARDOR(NpcID.GENERAL_GRAARDOR, 90, ChronoUnit.SECONDS),
	KRIL_TSUTSAROTH(NpcID.KRIL_TSUTSAROTH, 90, ChronoUnit.SECONDS),
	KREEARRA(NpcID.KREEARRA, 90, ChronoUnit.SECONDS),
	COMMANDER_ZILYANA(NpcID.COMMANDER_ZILYANA, 90, ChronoUnit.SECONDS),
	CALLISTO(NpcID.CALLISTO_6609, 29, RSTimeUnit.GAME_TICKS),
	ARTIO(NpcID.ARTIO, 29, RSTimeUnit.GAME_TICKS),
	CHAOS_ELEMENTAL(NpcID.CHAOS_ELEMENTAL, 60, ChronoUnit.SECONDS),
	CHAOS_FANATIC(NpcID.CHAOS_FANATIC, 30, ChronoUnit.SECONDS),
	CRAZY_ARCHAEOLOGIST(NpcID.CRAZY_ARCHAEOLOGIST, 30, ChronoUnit.SECONDS),
	KING_BLACK_DRAGON(NpcID.KING_BLACK_DRAGON, 9, ChronoUnit.SECONDS),
	SCORPIA(NpcID.SCORPIA, 16, RSTimeUnit.GAME_TICKS),
	VENENATIS(NpcID.VENENATIS_6610, 28, RSTimeUnit.GAME_TICKS),
	SPINDEL(NpcID.SPINDEL, 28, RSTimeUnit.GAME_TICKS),
	VETION(NpcID.VETION_6612, 33, RSTimeUnit.GAME_TICKS),
	CALVARION(NpcID.CALVARION_11994, 33, RSTimeUnit.GAME_TICKS),
	DAGANNOTH_PRIME(NpcID.DAGANNOTH_PRIME, 90, ChronoUnit.SECONDS),
	DAGANNOTH_REX(NpcID.DAGANNOTH_REX, 90, ChronoUnit.SECONDS),
	DAGANNOTH_SUPREME(NpcID.DAGANNOTH_SUPREME, 90, ChronoUnit.SECONDS),
	CORPOREAL_BEAST(NpcID.CORPOREAL_BEAST, 30, ChronoUnit.SECONDS),
	GIANT_MOLE(NpcID.GIANT_MOLE, 9000, ChronoUnit.MILLIS),
	DERANGED_ARCHAEOLOGIST(NpcID.DERANGED_ARCHAEOLOGIST, 29400, ChronoUnit.MILLIS),
	CERBERUS(NpcID.CERBERUS, 8400, ChronoUnit.MILLIS),
	THERMONUCLEAR_SMOKE_DEVIL(NpcID.THERMONUCLEAR_SMOKE_DEVIL, 8400, ChronoUnit.MILLIS),
	KRAKEN(NpcID.KRAKEN, 8400, ChronoUnit.MILLIS),
	KALPHITE_QUEEN(NpcID.KALPHITE_QUEEN_965, 30, ChronoUnit.SECONDS),
	DUSK(NpcID.DUSK_7889, 5, ChronoUnit.MINUTES),
	ALCHEMICAL_HYDRA(NpcID.ALCHEMICAL_HYDRA_8622, 25200, ChronoUnit.MILLIS),
	SARACHNIS(NpcID.SARACHNIS, 16, RSTimeUnit.GAME_TICKS),
	ZALCANO(NpcID.ZALCANO_9050, 21600, ChronoUnit.MILLIS),
	PHANTOM_MUSPAH(NpcID.PHANTOM_MUSPAH_12080, 50, RSTimeUnit.GAME_TICKS),
	THE_LEVIATHAN(NpcID.THE_LEVIATHAN, 30, RSTimeUnit.GAME_TICKS)
	;

	private static final Map<Integer, Boss> bosses;

	@Getter
	private final int id;
	private final Duration spawnTime;

	static
	{
		ImmutableMap.Builder<Integer, Boss> builder = new ImmutableMap.Builder<>();

		for (Boss boss : values())
		{
			builder.put(boss.getId(), boss);
		}

		bosses = builder.build();
	}

	Boss(int id, long period, TemporalUnit unit)
	{
		this.id = id;
		this.spawnTime = Duration.of(period, unit);
	}

	/**
	 * @return Respawn time in game ticks
	 */
	public double getSpawnTime()
	{
		return (double) spawnTime.toMillis() / Constants.GAME_TICK_LENGTH;
	}

	public static Boss find(int id)
	{
		return bosses.get(id);
	}
}
