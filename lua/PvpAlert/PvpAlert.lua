--자동으로 지정되는거 같다
local addonName = ...
local author = "매일매일배고파 - 세나리우스"

local addon = CreateFrame("frame",addonName)
scheduleTimer = LibStub("AceTimer-3.0").ScheduleTimer

function userCommand(msg)
  if msg == '' then msg = '명령어를 입력해주세요' end
  print(u.color(Color.pink,msg))
end

--좃같다. SLASH_#{이름}1 이런식으로 설정해야된다. ㅅㅂ
SLASH_PVP_ALERT1 = "/pp"
SlashCmdList["PVP_ALERT"] = userCommand

local onEvent={}
local COMBAT_LOG_EVENT_UNFILTERED={}
local customRegEvents = {"COMBAT_LOG_EVENT_UNFILTERED","UNIT_HEALTH","PLAYER_ENTERING_WORLD",}
local customCombatEvents = {"UNIT_DIED"}

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
    scheduleTimer(OnCallback_PlaySoundFile, 3, soundPath.."당신쥬금"..math.random(0,3)..".mp3")
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
  print('['..u.color(Color.pink,addonName)..'] 제작 : '..u.color(Color.sky,author))
end
for i=1,#customRegEvents do addon:RegisterEvent(customRegEvents[i])end
addon:SetScript("OnEvent",
  function(frame, event, ...)
    onEvent[event](onEvent, ...)
  end
)
