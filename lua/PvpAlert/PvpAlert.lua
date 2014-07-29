--======= 공통 설정파일 ==========
local addonName = ...
local _thisFrame = CreateFrame("frame",addonName)
local soundPath = "Interface\\AddOns\\"..addonName.."\\sounds\\" -- /로 수정해보기

--칼라 입히기
local author = "매일매일배고파 - 세나리우스"
--local scheduleTimer = LibStub("AceTimer-3.0").ScheduleTimer
local AceTimer = {ScheduleTimer = LibStub("AceTimer-3.0").ScheduleTimer}

function userCommand(msg)
  if msg == '' then msg = '명령어를 입력해주세요' end
  print(TextColor.sky(msg))
  local randomSoundPath = soundPath.."죽지마"..math.random(0,4)..".mp3"
  AceTimer:ScheduleTimer(PlaySoundFile,1,randomSoundPath,"Master")
end

--좃같다. SLASH_#{이름}1 이런식으로 설정해야된다. ㅅㅂ
SLASH_PVP_ALERT1 = "/pp"
SlashCmdList["PVP_ALERT"] = userCommand

------------------------------------------------------------------------------
-- 이벤트 정의
------------------------------------------------------------------------------
local OnEvents = {}

OnEvents.UNIT_HEALTH = function(unit)

  if unit ~= "player" then return end

  --이하 리팩토링 하기
  local lhper1 = 0.65
  local lhper2 = 0.3
  local lowhealth1 = 0
  local lowhealth2 = 0

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

OnEvents.COMBAT_LOG_EVENT_UNFILTERED = function(...)
  --한번 죽어보기
  --print(select(2, ...))
  local eventType = select(2, ...)
  if eventType == 'UNIT_DIED' then
    local randomSoundPath = soundPath.."당신쥬금"..math.random(0,3)..".mp3"
    AceTimer:ScheduleTimer(PlaySoundFile,3,randomSoundPath,"Master")
  end
end

OnEvents.PLAYER_ENTERING_WORLD = function(...)
  print('['..TextColor.pink(addonName)..'] 제작 : '..TextColor.sky(author))
end

------------------------------------------------------------------------------
-- 이벤트 등록.  이벤트 key를 등록하면 콜백이 넘어오는듯?
------------------------------------------------------------------------------
for key in pairs(OnEvents) do
  _thisFrame:RegisterEvent(key)
end
_thisFrame:SetScript("OnEvent",
  function(frame, event, ...)
    --파라메터 뭐있는지 찍어보자
    OnEvents[event](...)
  end
)
