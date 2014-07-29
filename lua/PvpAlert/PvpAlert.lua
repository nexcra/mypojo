--======= 공통 설정파일 ==========
local addonName = ...
local wow = CreateFrame("frame",addonName)
local soundPath = "Interface\\AddOns\\"..addonName.."\\sounds\\" -- /로 수정해보기
local author = "매일매일배고파 - 세나리우스"
local scheduleTimer = LibStub("AceTimer-3.0").ScheduleTimer

function userCommand(msg)
  if msg == '' then msg = '명령어를 입력해주세요' end
  print(u.color(Color.pink,msg))

  --테스트
  scheduleTimer(OnCallback_PlaySoundFile, 3, soundPath.."당신쥬금"..math.random(0,3)..".mp3")
end

--좃같다. SLASH_#{이름}1 이런식으로 설정해야된다. ㅅㅂ
SLASH_PVP_ALERT1 = "/pp"
SlashCmdList["PVP_ALERT"] = userCommand

local onEvents = {}

onEvents.UNIT_HEALTH = function(event, ...)
  local unit = ...
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

onEvents.COMBAT_LOG_EVENT_UNFILTERED = function(event, ...)
  --한번 죽어보기
  local eventType = select(2, ...)
  if eventType == 'UNIT_DIED' then
      local destGUID = select(8, ...)
      -- 이거 정상화시켜주기
      local function OnCallback_PlaySoundFile(path)
        PlaySoundFile(path,"Master")
      end
      if ns.playerGUID==destGUID then
        scheduleTimer(OnCallback_PlaySoundFile, 3, soundPath.."당신쥬금"..math.random(0,3)..".mp3")
      end  
  end
end

onEvents.PLAYER_ENTERING_WORLD = function(event, ...)
  print('['..u.color(Color.pink,addonName)..'] 제작 : '..u.color(Color.sky,author))
end

-- ========== 이벤트 등록 ===========
--이벤트 key를 등록하면 콜백이 넘어오는듯?
for key in pairs(onEvents) do
  wow:RegisterEvent(key)
end 
--콜백 처리
wow:SetScript("OnEvent",
  function(frame, event, ...)
    onEvents[event](...)
  end
)
