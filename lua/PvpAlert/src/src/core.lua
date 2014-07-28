local addonName, ns = ...
ns.addon=CreateFrame("frame",addonName)
local addon = ns.addon
ns.ScheduleTimer = LibStub("AceTimer-3.0").ScheduleTimer

local onEvent={}
local COMBAT_LOG_EVENT_UNFILTERED={}
local customRegEvents = {"COMBAT_LOG_EVENT_UNFILTERED","UNIT_HEALTH","PLAYER_ENTERING_WORLD",}
local customCombatEvents = {"UNIT_DIED"}

local author="create by |cff00ccccMoosi-Kargath"
local loaded = "|cffff66cc["..addonName.."]|r|cffcc99cc-"..author.."]|r"

local function OnCallback_PlaySoundFile(path)
  PlaySoundFile(path,"Master")
end

local soundPath="Interface\\AddOns\\"..addonName.."\\sounds\\"

local lhper1 = 0.65
local lhper2 = 0.3
local lowhealth1=0
local lowhealth2=0

function COMBAT_LOG_EVENT_UNFILTERED.UNIT_DIED(event, ...)
  local destGUID=select(8, ...)
  if ns.playerGUID==destGUID then
    ns:ScheduleTimer(OnCallback_PlaySoundFile, 3, soundPath.."당신쥬금"..math.random(0,3)..".mp3")
  end
end

function onEvent.UNIT_HEALTH(event, ...)
  local unit=...
  if unit ~= "player" then return end

  if (UnitHealth(unit)/UnitHealthMax(unit) > lhper1) then
    lowhealth2=0
    lowhealth1=0
  end

  if (UnitHealth(unit)/UnitHealthMax(unit) > lhper2) then
    lowhealth2=0
  end

  --아프지마
  if (UnitHealth(unit)/UnitHealthMax(unit) <= lhper1) and lowhealth1==0 then
    lowhealth1 = 1
  end

  --죽지마
  if (UnitHealth(unit)/UnitHealthMax(unit) <= lhper2) and lowhealth2==0 then
    lowhealth2 = 1

  end

  if lowhealth1==1 then
    if lowhealth1==1 and lowhealth2==1 then
      lowhealth1=2
    else
      lowhealth1=2
      if(not UnitIsDeadOrGhost("player")) then
        PlaySoundFile(soundPath.."아프지마"..math.random(0,6)..".mp3","Master")
      end
    end

  end

  if lowhealth2==1 then
    lowhealth2=2
    if(not UnitIsDeadOrGhost("player")) then
      PlaySoundFile(soundPath.."죽지마"..math.random(0,4)..".mp3","Master")
    end
  end
end

function onEvent.COMBAT_LOG_EVENT_UNFILTERED(event, ...)local eventType=select(2, ...)for i=1,#customCombatEvents do if eventType==customCombatEvents[i] then COMBAT_LOG_EVENT_UNFILTERED[eventType](event, ...) end end end
function onEvent.PLAYER_ENTERING_WORLD(event, ...)
  print(loaded)
end
for i=1,#customRegEvents do addon:RegisterEvent(customRegEvents[i])end
addon:SetScript("OnEvent",
  function(frame, event, ...)
    onEvent[event](onEvent, ...)
  end
)
