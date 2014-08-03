--======= 공통 설정파일 ==========
local addonName , ns = ...
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

OnEvents.COMBAT_LOG_EVENT_UNFILTERED = function(...)
  --한번 죽어보기
  local timestamp, eventType, isHideCaster, sourceGUID, sourceName, sourceFlags, sourceFlags2, destGUID, destName, destFlags, destFlags2 = select(1, ...)
  local args = {...}
  --print(args[5])
  --print(type .. ' : ' .. sourceName)
  if(sourceName == '변신이안되요') then
    print(table.concat({...},','))
  end
  
  if eventType == 'UNIT_DIED' and ns.playerGUID==destGUID then
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
