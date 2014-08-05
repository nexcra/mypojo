------------------------------------------------------------------------------
-- Badapples
--
-- Stores the name (and optional description) about players you've encountered
-- or been told about that you want to make sure to remember so as to avoid
-- ever grouping with them (or otherwise).
--
-- For control, the AddOn registers a /badapples slash command (aliased as
-- /badapple and /bad) with the following options:
--     /badapples help (default command, describes available options)
--     /badapples list (lists all badapples currently known to chat window)
--     /badapples show (shows all badapples in social window)
--     /badapples add <playername> [comment] (adds a player to the list)
--     /badapples remove <playername> (removes a player from the list)
--     /badapples check <playername> (shows player status in badapples list)
--     /badapples removeall (removes all players in the badapples list)
--     /badapples color (allows the player to set the color for badapples)
--     /badapples toggletab (allows player to change position of social tab)
--     /badapples notab (allows player to disable social tab)
--
-- Written by Cirk of DoomHammer, April 2005
-- Last updated October 2008
------------------------------------------------------------------------------

------------------------------------------------------------------------------
-- Globals
------------------------------------------------------------------------------
Badapples = {};								-- Badapples global function table
BadapplesState = {};						-- Will be overridden when loaded

BADAPPLES_FRAME_SCROLL_HEIGHT = 16;
BADAPPLES_DISPLAY_COUNT = 17; 

------------------------------------------------------------------------------
-- Text strings
------------------------------------------------------------------------------
local EM_ON = "|cffffff00"
local EM_ONh = "|cFFFF8C8C"
local EM_OFF = "|r"
local RED_ON = "|cffff4000"
local RED_OFF = "|r"
local BAD_ON = "|cffff991a"					-- Will be overridden on load
local BAD_OFF = "|r"

BADAPPLES_TEXT = {
  BADAPPLES_TAB_SHORTNAME = "무개념",

  COMMAND_REMOVEALL_CONFIRM = "confirm",

  ADD_CONFIRM = EM_ON.."무개념 리스트 : "..EM_OFF.."%s"..EM_ON.." (이)가 리스트에 추가되었습니다. 메모 : "..EM_OFF.."%s",
  UPDATE_CONFIRM = EM_ON.."무개념 리스트 : "..EM_OFF.."%s"..EM_ON.." 에 대한 메모가 ["..EM_OFF.."%s"..EM_ON.."] 으로 수정되었습니다. "..EM_OFF,
  REMOVE_CONFIRM = EM_ON.."무개념 리스트 : "..EM_OFF.."%s"..EM_ON.." (을)를 리스트에서 삭제하였습니다."..EM_OFF,
  REMOVE_NOTFOUND = EM_ON.."무개념 리스트 : "..EM_OFF.."%s"..EM_ON.." (은)는 리스트에 없거나 잘못 입력하셨습니다."..EM_OFF,
  PLAYERNAME_FAILED = EM_ON.."무개념 리스트 : 정확한 아이디를 입력해 주십시오."..EM_OFF,
  PLAYERNAME_ISSELF = EM_ON.."무개념 리스트 : 자기 자신을 리스트에 추가할 수 없습니다."..EM_OFF,
  LIST_FORMAT = "   %s"..EM_ON..": "..EM_OFF.."%s",
  STATUS_GOOD = EM_ON.."무개념 리스트 : "..EM_OFF.."%s"..EM_ON.." (은)는 리스트에 등록되어 있지 않습니다.",
  REMOVEALL_CONFIRM = EM_ON.."무개념 리스트 : 리스트에 등록된 모든 내용이 삭제되었습니다."..EM_OFF,
  PARTY_WARNING = "파티원 %s(은)는 당신의 무개념 목록에 있습니다: %s",
  PARTY_WARNING_NO_REASON = "파티원 %s(은)는 당신의 무개념 목록에 있습니다.",
  PARTY_IGNORE_WARNING = "파티원 %s(은)는 당신의 차단 목록에 있습니다.",
  RAID_WARNING = "공격대원 %s(은)는 당신의 무개념 목록에 있습니다: %s",
  RAID_WARNING_NO_REASON = "공격대원 %s(은)는 당신의 무개념 목록에 있습니다.",
  RAID_IGNORE_WARNING = "공격대원 %s(은)는 당신의 차단 목록에 있습니다.",
  NOTIFY_BAD = "플레이어 %s(은)는 당신의 무개념 목록에 있습니다: %s",
  NOTIFY_IGNORE = "플레이어 %s(은)는 당신의 차단목록에 있습니다.",

  NO_REASON = "(작성된 메모 없음)",
  PARTY_INVITE_TEXT = "무개념 리스트에 등록되어 있는 |cff30c0ff%s|r (이)가 \n당신을 초대했습니다.",
  PARTY_IGNORE_INVITE_TEXT = "차단된 상태인 |cff30c0ff%s|r (이)가 당신을 초대했습니다.",
  PARTY_INVITE_BUTTON = "할수없지 뭐",
  INVITE_TEXT = "|cff30c0ff%s|r (은)는 무개념 리스트에 등록되어 있습니다. \n정말 초대하시겠습니까?",
  INVITE_IGNORE_TEXT = "|cff30c0ff%s|r (은)는 차단된 상태입니다. \n정말 초대하시겠습니까?",
  PLAYER_ADD_TEXT = "리스트에 추가할 아이디를 입력해 주십시오.",
  PLAYER_ADD_CONFIRM_TEXT = "%s (을)를 리스트에 추가하시겠습니까?",
  PLAYER_REMOVE_CONFIRM_TEXT = "|cff30c0ff%s|r (을)를 리스트에 삭제하시겠습니까?",
  REMOVEALL_CONFIRM_TEXT = "리스트에 등록된 |cffff8080모든 내용을 삭제하려고 합니다|r, \n정말 삭제하시겠습니까?",
  ENABLE_BOTTOM_TAB = "커뮤니티창 하단",
};

------------------------------------------------------------------------------
-- Local constants
------------------------------------------------------------------------------
local BADAPPLES_MAXIMUM_REASON_LENGTH = 320;
local BADAPPLES_PARTY_CLEANUP_DELAY = 1;
local BADAPPLES_RAID_CLEANUP_DELAY = 1;
local BADAPPLES_DEFAULT_HIGHLIGHT_R = 1;
local BADAPPLES_DEFAULT_HIGHLIGHT_G = 0.6;
local BADAPPLES_DEFAULT_HIGHLIGHT_B = 0.3;
local BADAPPLES_TAB_ID = 17;
local BADAPPLES_REWARN_DELAY = 10;			-- Minimum warning interval for the same badapple


------------------------------------------------------------------------------
-- Local variables
------------------------------------------------------------------------------
local _original_GetColoredName;				-- Original GetColoreName function
local _original_InviteUnit;					-- Original InviteUnit function
local _original_GameTooltip_UnitColor;		-- Original GameTooltip_UnitColor function

local _thisFrame = nil;						-- The frame pointer
local _serverName = nil;					-- set to current realm when loaded
local _playerName = nil;					-- set to name of player when known
local _tabNoBottomTab = nil;				-- if bottom tab cannot be enabled due to another mod
local _tabEnabled = nil;					-- tab disabled or enabled status
local _listCount = 0;						-- current number of entries in list
local _highlightColors = {};				-- RGB colors to use for highlighting Badapples
local _partyMembers = {};					-- List of party members;
local _partyMembersTimeout = nil;			-- For when to purge old members
local _raidMembers = {};					-- List of raid members;
local _raidMembersTimeout = nil;			-- For when to purge old members
local _listSorted = {};						-- Table of sorted indices by sort order
local _ignoreList = {};						-- Populated with players on your ignore list
local _lastBadappleWarning = nil;			-- Text used in last badapple warning
local _lastBadappleWarningExpires = nil;	-- Time at which the last badapple warning expires

local _first = 1;


------------------------------------------------------------------------------
-- Chat events of interest
------------------------------------------------------------------------------
local _chatEventList = {
  ["CHAT_MSG_SAY"] = 1,
  ["CHAT_MSG_WHISPER"] = 1,
  ["CHAT_MSG_WHISPER_INFORM"] = 1,
  ["CHAT_MSG_YELL"] = 1,
  ["CHAT_MSG_PARTY"] = 1,
  ["CHAT_MSG_RAID"] = 1,
  ["CHAT_MSG_GUILD"] = 1,
  ["CHAT_MSG_CHANNEL"] = 1,
  ["CHAT_MSG_AFK"] = 1,
  ["CHAT_MSG_DND"] = 1,
};

------------------------------------------------------------------------------
-- StaticPopup definitions
------------------------------------------------------------------------------
local _badapplesInvitePopup = {
  text = BADAPPLES_TEXT.INVITE_TEXT,
  button1 = YES,
  button2 = NO,
  OnAccept = function(self, data)
    _original_InviteUnit(data);
  end,
  sound = "igPlayerInvite",
  timeout = 20,
  showAlert = 1,
  hideOnEscape = 1,
  whileDead = 1,
};

local _badapplesAddPlayerPopup = {
  text = BADAPPLES_TEXT.PLAYER_ADD_TEXT,
  button1 = ACCEPT,
  button2 = CANCEL,
  hasEditBox = 1,
  maxLetters = 12,
  OnShow = function(self)
    _G[self:GetName().."EditBox"]:SetFocus();
  end,
  OnHide = function(self)
    local i = 1;
    local f = _G["ChatFrame"..i];
    if (_G[f:GetName().."EditBox"]:IsShown()) then
      _G[f:GetName().."EditBox"]:SetFocus();
    end
    getglobal(self:GetName().."EditBox"):SetText("");
  end,
  OnAccept = function(self)
    local editBox = _G[self:GetName().."EditBox"];
    Badapples.Frame_EditBoxAddName(editBox);
  end,
  EditBoxOnEnterPressed = function(self)
    Badapples.Frame_EditBoxAddName(self);
    self:GetParent():Hide();
  end,
  EditBoxOnEscapePressed = function(self)
    self:GetParent():Hide();
  end,
  timeout = 60,
  exclusive = 1,
  whileDead = 1,
};

local _badapplesConfirmAddPopup = {
  text = BADAPPLES_TEXT.PLAYER_ADD_CONFIRM_TEXT,
  button1 = YES,
  button2 = NO,
  OnAccept = function(self, data)
    Badapples.Add(data);
  end,
  sound = "igCharacterInfoOpen",
  timeout = 60,
  exclusive = 1,
  hideOnEscape = 1,
  whileDead = 1,
};

local _badapplesConfirmRemovePopup = {
  text = BADAPPLES_TEXT.PLAYER_REMOVE_CONFIRM_TEXT,
  button1 = YES,
  button2 = NO,
  OnAccept = function(self, data)
    Badapples.Remove(data);
  end,
  sound = "igCharacterInfoOpen",
  timeout = 60,
  exclusive = 1,
  hideOnEscape = 1,
  whileDead = 1,
};

local _badapplesConfirmRemoveAllPopup = {
  text = BADAPPLES_TEXT.REMOVEALL_CONFIRM_TEXT,
  button1 = YES,
  button2 = NO,
  OnAccept = function(self)
    Badapples.RemoveAll(BADAPPLES_TEXT.COMMAND_REMOVEALL_CONFIRM);
  end,
  sound = "igCharacterInfoOpen",
  timeout = 60,
  exclusive = 1,
  hideOnEscape = 1,
  whileDead = 1,
};


------------------------------------------------------------------------------
-- Utility functions
------------------------------------------------------------------------------
--???
function Badapples.GetNextParam(text)
  -- Extracts the next parameter out of the passed text, and returns it and
  -- the rest of the string
  for param, remain in string.gmatch(text, "([^%s]+)%s+(.*)") do
    return param, remain;
  end
  return text;
end

--?? 안쓰일듯
function Badapples.FormatName(text)
  -- Formats the indicated name as a player name (only first letter can be
  -- uppercase).  Here we are careful to handle UTF-8 coding (which is what
  -- WoW uses in 2.2) manually since string.sub doesn't do this itself.
  local firstChar = string.sub(text, 1, 1);
  local remain = string.sub(text, 2);
  if (string.byte(firstChar) >= 192) then
    firstChar = string.sub(text, 1, 3);
    remain = string.sub(text, 4);
  end
  return string.upper(firstChar)..string.lower(remain);
end

--요건 참조할만함.. 근데?
function Badapples.NameToLink(name)
  -- Converts the passed name to a player name link (that can be clicked on)
  -- but without showing the usual [..] around the name
  return "|Hplayer:"..name.."|h"..name.."|h";
end

--무슨효과일지 써보자
function Badapples.UpdateHighlightText()
  local r = floor((255 * _highlightColors.r) + 0.5);
  local g = floor((255 * _highlightColors.g) + 0.5);
  local b = floor((255 * _highlightColors.b) + 0.5);
  BAD_ON = format("|cff%02x%02x%02x", r, g, b);
end

function Badapples.SetHighLightColor(values)
  if (values) then
    _highlightColors.r = values.r;
    _highlightColors.g = values.g;
    _highlightColors.b = values.b;
  else
    _highlightColors.r, _highlightColors.g, _highlightColors.b = ColorPickerFrame:GetColorRGB();
  end
  Badapples.UpdateHighlightText();
  Badapples.TargetFrame_CheckFaction();
  -- Update the color of the player names in the social screen
  if (BadapplesFrame:IsShown()) then
    Badapples.Frame_ListUpdate();
  end
end


function Badapples.NotifyPlayer(notifyText, name, reason, errorText, frame)
  -- Notify the player about a badapple event by showing a formatted text
  -- message in the current chat frame (notifyText) and/or in the UI error
  -- frame (errorText).
  if (notifyText) then
    local text = string.format(RED_ON..notifyText..RED_OFF, BAD_ON..name..RED_ON, RED_OFF..(reason or ""));
    local time = GetTime();
    if (not _lastBadappleWarningExpires or (time > _lastBadappleWarningExpires) or (text ~= _lastBadappleWarning) or frame) then
      _lastBadappleWarning = text;
      _lastBadappleWarningExpires = time + BADAPPLES_REWARN_DELAY;
      --이거 모듈화 해보기
      if (frame) then
        frame:AddMessage(text);
      else
        for i = 1, NUM_CHAT_WINDOWS do
          local chatFrame = _G["ChatFrame"..i];
          if (chatFrame:IsShown()) then
            chatFrame:AddMessage(text);
            break;
          end
        end
      end
    end
  end
  if (errorText) then
    local text = string.format(RED_ON..errorText..RED_OFF, name);
    UIErrorsFrame:AddMessage(text);
  end
end


function Badapples.UpdateIgnoreList()
  -- Copies the currently ignored player names into the local _ignoreList
  _ignoreList = {};
  for index = 1, GetNumIgnores() do
    local name = GetIgnoreName(index);
    if (name and (name ~= "")) then
      name = Badapples.FormatName(name);
      _ignoreList[name] = 1;
    end
  end
end


function Badapples.CompareOnNameAtoZ(name1, name2)
  return name1 < name2;
end


function Badapples.CompareOnNameZtoA(name1, name2)
  return name2 < name1;
end


function Badapples.CompareOnReasonAtoZ(name1, name2)
  local reason1 = BadapplesState.List[name1].Reason;
  local reason2 = BadapplesState.List[name2].Reason;
  if (not reason1) then
    return false;
  end
  if (not reason2) then
    return true;
  end
  return reason1 < reason2;
end


function Badapples.CompareOnReasonZtoA(name1, name2)
  local reason1 = BadapplesState.List[name1].Reason;
  local reason2 = BadapplesState.List[name2].Reason;
  if (not reason2) then
    return false;
  end
  if (not reason1) then
    return true;
  end
  return reason2 < reason1;
end


function Badapples.SortList()
  -- Creates the _listSorted list that can be used to return the values of
  -- the Badapples list in the appropriate sorted order
  _listSorted = {};
  for name in pairs(BadapplesState.List) do
    table.insert(_listSorted, name);
  end

  table.sort(_listSorted, Badapples.CompareOnNameAtoZ);
end

--확인해보기
function Badapples.CheckForOtherModTab()
  -- Returns 1 if there is another mod already using a 6th social tab, or
  -- nil otherwise
  if (FriendsFrameTab6 or (FriendsFrame.numTabs == 6)) then
    return 1;
  end
  return nil;
end

------------------------------------------------------------------------------
-- Badapples interface functions
------------------------------------------------------------------------------
function Badapples.List()
  -- Lists the current players on the Badapples list and the reasons they
  -- are there.  This call always resorts the list into by name order.
  -- 정렬
  Badapples.SortList();
  if (_listCount > 0) then
    if (_listCount == 1) then
      DEFAULT_CHAT_FRAME:AddMessage(EM_ON.."무개념 리스트 : |cffff80801|r 명이 등록되어 있습니다."..EM_OFF);
    else
      DEFAULT_CHAT_FRAME:AddMessage(EM_ON.."무개념 리스트 : |cffff8080".._listCount.."|r 명이 등록되어 있습니다."..EM_OFF);
    end
  else
    DEFAULT_CHAT_FRAME:AddMessage(EM_ON.."무개념 리스트에 등록된 플레이어가 없습니다."..EM_OFF);
    return;
  end
  for index, name in ipairs(_listSorted) do
    local reason = BadapplesState.List[name].Reason;
    if (not reason) then
      reason = BADAPPLES_TEXT.NO_REASON;
    end
    DEFAULT_CHAT_FRAME:AddMessage(format(BADAPPLES_TEXT.LIST_FORMAT, BAD_ON..name..BAD_OFF, reason));
  end
end


function Badapples.Status(name, silent)
  -- Queries and displays the status of the indicated player, returning 1 if
  -- the player is in the Badapples list or nil otherwise.  If the silent
  -- parameter is not nil then no output is generated.
  if (not name or (name == "")) then
    if (not silent) then
      DEFAULT_CHAT_FRAME:AddMessage(BADAPPLES_TEXT.PLAYERNAME_FAILED);
    end
    return nil;
  end
  local player = Badapples.FormatName(name);
  if (BadapplesState.List[player]) then
    if (not silent) then
      local reason = BadapplesState.List[player].Reason or BADAPPLES_TEXT.NO_REASON;
      Badapples.NotifyPlayer(BADAPPLES_TEXT.NOTIFY_BAD, player, reason, nil, DEFAULT_CHAT_FRAME);
    end
    return 1;
  else
    if (not silent) then
      DEFAULT_CHAT_FRAME:AddMessage(format(BADAPPLES_TEXT.STATUS_GOOD, player));
    end
  end
  return nil;
end


function Badapples.Add(name_and_reason)
  -- Adds the indicated player (with reason if provided) to the Badapples
  -- list, returning 1 if the player was added successfully (or is already
  -- present) or nil otherwise.
  if (not name_and_reason or (name_and_reason == "")) then
    DEFAULT_CHAT_FRAME:AddMessage(BADAPPLES_TEXT.PLAYERNAME_FAILED);
    return nil;
  end
  local name, reason = Badapples.GetNextParam(name_and_reason);
  local player = Badapples.FormatName(name);
  if (player == _playerName) then
    DEFAULT_CHAT_FRAME:AddMessage(BADAPPLES_TEXT.PLAYERNAME_ISSELF);
    return nil;
  end
  if (reason and (string.len(reason) > BADAPPLES_MAXIMUM_REASON_LENGTH)) then
    reason = string.sub(reason, 1, BADAPPLES_MAXIMUM_REASON_LENGTH);
  end
  if (BadapplesState.List[player]) then
    BadapplesState.List[player].Reason = reason;
    if (not reason) then
      reason = BADAPPLES_TEXT.NO_REASON;
    end
    DEFAULT_CHAT_FRAME:AddMessage(format(BADAPPLES_TEXT.UPDATE_CONFIRM, BAD_ON..player..BAD_OFF, reason));
  else
    BadapplesState.List[player] = {};
    BadapplesState.List[player].Reason = reason;
    if (_listCount == 0) then
      -- First player added to list, so register events as well
      _listCount = 1;
    else
      _listCount = _listCount + 1;
    end
    Badapples.SortList();	-- 정렬
    if (not reason) then
      reason = BADAPPLES_TEXT.NO_REASON;
    end
    DEFAULT_CHAT_FRAME:AddMessage(format(BADAPPLES_TEXT.ADD_CONFIRM, BAD_ON..player..BAD_OFF, reason));
  end
  -- Update the Badapples frame and target display
  if (BadapplesFrame:IsShown()) then
    BadapplesFrame.SelectedName = player;
    Badapples.Frame_ListUpdate();
  end
  Badapples.TargetFrame_CheckFaction();
  return 1;
end


function Badapples.Remove(name)
  if (not name or (name == "")) then
    DEFAULT_CHAT_FRAME:AddMessage(BADAPPLES_TEXT.PLAYERNAME_FAILED);
    return;
  end
  local player = Badapples.FormatName(name);
  if (BadapplesState.List[player]) then
    BadapplesState.List[player] = nil;
    if (_listCount == 1) then
      -- Last player removed, so unregister events as well
      _listCount = 0;
    else
      _listCount = _listCount - 1;
    end
    Badapples.SortList();	-- 정렬
    DEFAULT_CHAT_FRAME:AddMessage(format(BADAPPLES_TEXT.REMOVE_CONFIRM, player));
    -- Update the Badapples frame and target display
    if (BadapplesFrame:IsShown()) then
      Badapples.Frame_ListUpdate();
    end
    Badapples.TargetFrame_CheckFaction();
  else
    DEFAULT_CHAT_FRAME:AddMessage(format(BADAPPLES_TEXT.REMOVE_NOTFOUND, player));
  end
end


function Badapples.RemoveAll(confirm)
  -- Removes all the player entries in the list
  if (_listCount > 0) then
    if (not confirm or (confirm ~= BADAPPLES_TEXT.COMMAND_REMOVEALL_CONFIRM)) then
      StaticPopup_Show("BADAPPLE_REMOVEALL");
      return;
    end
    BadapplesState.List = {};
    _listCount = 0;
    -- Update the sorted list to be empty to
    Badapples.SortList();	-- 정렬
    -- Update the Badapples frame and target display
    if (BadapplesFrame:IsShown()) then
      Badapples.Frame_ListUpdate();
    end
    Badapples.TargetFrame_CheckFaction();
  end
  DEFAULT_CHAT_FRAME:AddMessage(BADAPPLES_TEXT.REMOVEALL_CONFIRM);
end


------------------------------------------------------------------------------
-- Check party and raid functions
------------------------------------------------------------------------------
function Badapples.CheckParty()
  -- Checks all party members as to whether or not they are Badapples,
  -- keeping track of which ones we have already warned the player about.
  -- Note that because the PARTY_MEMBERS_CHANGED event may signal first that
  -- there are no other players (even if there are) and then that there are
  -- other players (if there are) we do not want to remove players from the
  -- _partyMembers list immediately, but set a flag and delete them at a
  -- later time (via Badapples.OnUpdate).  So first we start by setting all
  -- the current members as pending deletion...
  for name in pairs(_partyMembers) do
    _partyMembers[name] = -1;
  end
  -- Now process all currently known members
  for i = 1, 4 do
    local name = UnitName("party"..i);
    if (name and (name ~= "")) then
      name = Badapples.FormatName(name);
      if (not _partyMembers[name]) then
        if (BadapplesState.List[name]) then
          -- Uh oh, new party member is on the Badapples list!
          Badapples.NotifyPlayer(BADAPPLES_TEXT.PARTY_WARNING, name, BadapplesState.List[name].Reason or BADAPPLES_TEXT.NO_REASON, BADAPPLES_TEXT.PARTY_WARNING_NO_REASON);
        elseif (_ignoreList[name]) then
          -- New party member is on the ignore list!
          Badapples.NotifyPlayer(BADAPPLES_TEXT.PARTY_IGNORE_WARNING, name, nil, BADAPPLES_TEXT.PARTY_IGNORE_WARNING);
        end
      end
      _partyMembers[name] = 1;
    end
  end
  -- Set the pending delete time and enable the OnUpdate hook
  _partyMembersTimeout = GetTime() + BADAPPLES_PARTY_CLEANUP_DELAY;
  _thisFrame:SetScript("OnUpdate", Badapples.OnUpdate);
end


function Badapples.CheckRaid()
  -- Checks all raid members as to whether or not they are Badapples using
  -- the same algorithm for cleanup as for party members (although probably
  -- not required in this case)
  for name in pairs(_raidMembers) do
    _raidMembers[name] = -1;
  end
  -- Now process all currently known members
  for i = 1, 40 do
    local name = GetRaidRosterInfo(i);
    if (name and (name ~= "")) then
      name = Badapples.FormatName(name);
      if (not _raidMembers[name]) then
        if (BadapplesState.List[name]) then
          -- Uh oh, new raid member is on the Badapples list!
          Badapples.NotifyPlayer(BADAPPLES_TEXT.RAID_WARNING, name, BadapplesState.List[name].Reason or BADAPPLES_TEXT.NO_REASON, BADAPPLES_TEXT.RAID_WARNING_NO_REASON);
        elseif (_ignoreList[name]) then
          -- New raid member is on the ignore list!
          Badapples.NotifyPlayer(BADAPPLES_TEXT.RAID_IGNORE_WARNING, name, nil, BADAPPLES_TEXT.RAID_IGNORE_WARNING);
        end
      end
      _raidMembers[name] = 1;
    end
  end
  -- Set the pending delete time and enable the OnUpdate hook
  _raidMembersTimeout = GetTime() + BADAPPLES_RAID_CLEANUP_DELAY;
  _thisFrame:SetScript("OnUpdate", Badapples.OnUpdate);
end


------------------------------------------------------------------------------
-- Initialization functions
------------------------------------------------------------------------------
--3개뿐??
function Badapples.RegisterEvents(register)
  -- If register is set, then register for events, otherwise unregister
  if (register == 1) then
    _thisFrame:RegisterEvent("GROUP_ROSTER_UPDATE");
    _thisFrame:RegisterEvent("PLAYER_TARGET_CHANGED");
    _thisFrame:RegisterEvent("IGNORELIST_UPDATE");
  else
    _thisFrame:UnregisterEvent("GROUP_ROSTER_UPDATE");
    _thisFrame:UnregisterEvent("PLAYER_TARGET_CHANGED");
    _thisFrame:UnregisterEvent("IGNORELIST_UPDATE");
  end
end


function Badapples.HookNeededFunctions()
  -- Replace GetColoredName function so we can ensure badapples get their
  -- names colored correctly in chat
  _original_GetColoredName = GetColoredName;
  GetColoredName = Badapples.GetColoredName;

  -- Post-hook ChatEdit_UpdateHeader so we can highlight Badapple player
  -- names if we send them a tell
  hooksecurefunc("ChatEdit_UpdateHeader", Badapples.ChatEdit_UpdateHeader);

  -- Post-hook SetItemRef so we can check player links for Badapples
  hooksecurefunc("SetItemRef", Badapples.SetItemRef);

  -- Post-hook StaticPopup_Show so we can check for party invites from
  -- Badapples and update the dialog text as needed.
  hooksecurefunc("StaticPopup_Show", Badapples.StaticPopup_Show);

  -- Replace InviteUnit so we can check for invitations to Badapples and
  -- Use the "BADAPPLE_INVITE" confirmation popup
  _original_InviteUnit = InviteUnit;
  InviteUnit = Badapples.InviteUnit;

  -- Post-hook TargetFrame_CheckFaction so we can check player names and
  -- override the frame color if we need
  hooksecurefunc("TargetFrame_CheckFaction", Badapples.TargetFrame_CheckFaction);

  -- Hook GameTooltip_UnitColor so we can introduce our own "faction style"
  -- highlighting for Badapples
  _original_GameTooltip_UnitColor = GameTooltip_UnitColor;
  GameTooltip_UnitColor = Badapples.GameTooltip_UnitColor;

  -- Post-hook PanelTemplates_UpdateTabs and so FriendsFrame_Update we can
  -- handle selection of our tabs.
  hooksecurefunc("PanelTemplates_UpdateTabs", Badapples.PanelTemplates_UpdateTabs);
  hooksecurefunc("FriendsFrame_Update", Badapples.FriendsFrame_Update);
end


function Badapples.VariablesLoaded()
  if (not BadapplesState) then
    BadapplesState = {};
  end
  if (not BadapplesState.Colors) then
    BadapplesState.Colors = {};
    BadapplesState.Colors.r = BADAPPLES_DEFAULT_HIGHLIGHT_R;
    BadapplesState.Colors.g = BADAPPLES_DEFAULT_HIGHLIGHT_G;
    BadapplesState.Colors.b = BADAPPLES_DEFAULT_HIGHLIGHT_B;
  end
  _highlightColors = BadapplesState.Colors;
  Badapples.UpdateHighlightText();
  if (not BadapplesState.List) then
    BadapplesState.List = {};
  end

  -- Hook all various functions we need
  Badapples.HookNeededFunctions();

  -- Update _listCount
  --??? 왜 이런짓을 하지?
  _listCount = 0;
  for name in pairs(BadapplesState.List) do
    _listCount = _listCount + 1;
  end
end


function Badapples.PlayerLogin()
  -- Get the user's setting for the social tab
  if (not BadapplesState.Tab) then
    BadapplesState.Tab = BADAPPLES_TEXT.ENABLE_BOTTOM_TAB;
  end
  -- Get the value of _tabNoBottomTab and then set the tab status
  -- according to the player's setting.  Note that if user selection is
  -- the bottom tab and it cannot be used then the side tab will
  -- automatically be selected instead but this is not updated in the
  -- player's setup (unless they change it).
  _tabNoBottomTab = Badapples.CheckForOtherModTab();
  Badapples.Frame_SetTab(BadapplesState.Tab);
end


------------------------------------------------------------------------------
-- Hooked functions for ChatFrame
------------------------------------------------------------------------------
---이거 수정해보기
function Badapples.GetColoredName(event, arg1, arg2, ...)
  if (_chatEventList[event]) then
    -- All these events have the player name in arg2
    local item = BadapplesState.List[arg2]
    if (item) then
      --return BAD_ON..arg2..BAD_OFF;
      local text = _original_GetColoredName(event, arg1, arg2, ...);
      if(item.Reason) then text = text..BAD_ON..' ('.. item.Reason .. ')'..BAD_OFF end 
      return text
    end
  end
  return _original_GetColoredName(event, arg1, arg2, ...);
end



------------------------------------------------------------------------------
-- Hook function for SetItemRef
------------------------------------------------------------------------------
--이거도 수정해서 해봄
function Badapples.SetItemRef(link, text, button)
  -- Warn player if they try whispering a badapple, or if the shift key is
  -- down and Badapple's player add box is open, then add them to it
  --print(link) 
  -- player:name:493:CHANNEL:1
  -- player:name:507:YELL  --숫자는 라인수인듯
  if (string.sub(link, 1, 6) == "player" ) then
    local name, lineid = strsplit(":", string.sub(link, 8)); --strsplit은 wow에서 제공하는거인듯
    if (name and (name ~= "")) then
      name = Badapples.FormatName(name);
      if (BadapplesState.List[name]) then
        -- Warn user about this badapple
        Badapples.NotifyPlayer(BADAPPLES_TEXT.NOTIFY_BAD, name, BadapplesState.List[name].Reason or BADAPPLES_TEXT.NO_REASON);
      elseif (_ignoreList[name]) then
        -- Warn user about this ignored player
        Badapples.NotifyPlayer(BADAPPLES_TEXT.NOTIFY_IGNORE, name);
      elseif (IsModifiedClick("CHATLINK")) then
        -- Add it to the "add" dialog if it is visible
        local staticPopup = StaticPopup_Visible("BADAPPLE_ADD");
        if (staticPopup) then
          _G[staticPopup.."EditBox"]:SetText(name);
        end
      end
    end
  end
end


------------------------------------------------------------------------------
-- Hook function for StaticPopup_Show
------------------------------------------------------------------------------
function Badapples.StaticPopup_Show(popupName, text_arg1, text_arg2, data)
  -- Called via hooksecurefunc after the original StaticPopup_Show is called
  -- this function looks for the "PARTY_INVITE" request, and checks to see
  -- if this was from a player on the badapples list.  If so, the dialog's
  -- text is changed to reflect this and a warning is shown.
  if ((popupName == "PARTY_INVITE") and text_arg1) then
    local name = Badapples.FormatName(text_arg1);
    if (name and (name ~= "")) then
      local replaceText = nil;
      if (BadapplesState.List[name]) then
        -- Warn user about this badapple
        Badapples.NotifyPlayer(BADAPPLES_TEXT.NOTIFY_BAD, name, BadapplesState.List[name].Reason or BADAPPLES_TEXT.NO_REASON);
        -- Use our replacement PARTY_INVITE dialog in badapple mode
        replaceText = string.format(BADAPPLES_TEXT.PARTY_INVITE_TEXT, BAD_ON..name..BAD_OFF);
      elseif (_ignoreList[name]) then
        -- Warn user about this ignored player
        Badapples.NotifyPlayer(BADAPPLES_TEXT.NOTIFY_IGNORE, name);
        -- Use our replacement PARTY_INVITE dialog in ignore mode
        replaceText = string.format(BADAPPLES_TEXT.PARTY_IGNORE_INVITE_TEXT, name);
      end
      if (replaceText) then
        -- Find the dialog being used and change its text and accept buttons
        for index = 1, STATICPOPUP_NUMDIALOGS, 1 do
          local frame = _G["StaticPopup"..index];
          if (frame:IsShown() and (frame.which == popupName)) then
            local text = _G[frame:GetName().."Text"];
            local button1 = _G[frame:GetName().."Button1"];
            local alertIcon = _G[frame:GetName().."AlertIcon"];
            if (text and text:IsShown()) then
              text:SetText(replaceText);
            end
            if (button1 and button1:IsShown()) then
              button1:SetText(BADAPPLES_TEXT.PARTY_INVITE_BUTTON);
              local width = button1:GetTextWidth();
              if (width > 120) then
                button1:SetWidth(width + 20);
              end
            end
            -- Call StaticPopup_Resize before we show the alert
            -- icon since the resize function will ignore it
            -- and would reduce the width again
            StaticPopup_Resize(frame, popupName);
            if (alertIcon) then
              alertIcon:Show();
              frame:SetWidth(420);
            end
            break;
          end
        end
      end
    end
  end
end


------------------------------------------------------------------------------
-- Hook function for InviteUnit
------------------------------------------------------------------------------
function Badapples.InviteUnit(name)
  if (name and (name ~= "")) then
    local playerName = Badapples.FormatName(name);
    if (BadapplesState.List[playerName]) then
      -- Warn user about this badapple in Chat
      Badapples.NotifyPlayer(BADAPPLES_TEXT.NOTIFY_BAD, playerName, BadapplesState.List[playerName].Reason or BADAPPLES_TEXT.NO_REASON);
      -- Warn user about inviting player by dialog box in badapple mode
      _badapplesInvitePopup.text = BADAPPLES_TEXT.INVITE_TEXT;
      local dialogFrame = StaticPopup_Show("BADAPPLE_INVITE", BAD_ON..playerName..BAD_OFF);
      if (dialogFrame) then
        dialogFrame.data = playerName;
      end
    elseif (_ignoreList[playerName]) then
      -- Warn user about this ignored player in Chat
      Badapples.NotifyPlayer(BADAPPLES_TEXT.NOTIFY_IGNORE, playerName);
      -- Warn user about inviting player by dialog box in ignore mode
      _badapplesInvitePopup.text = BADAPPLES_TEXT.INVITE_IGNORE_TEXT;
      local dialogFrame = StaticPopup_Show("BADAPPLE_INVITE", playerName);
      if (dialogFrame) then
        dialogFrame.data = playerName;
      end
    else
      return _original_InviteUnit(name);
    end
  else
    return _original_InviteUnit(name);
  end
end


------------------------------------------------------------------------------
-- Hook function for TargetFrame_CheckFaction
------------------------------------------------------------------------------
function Badapples.TargetFrame_CheckFaction(self)
  -- Sets the color of the target's name background if the target is a
  -- player on the Badapples list.
  if (UnitIsPlayer("target")) then
    local name = UnitName("target");
    if (name and (name ~= "")) then
      name = Badapples.FormatName(name);
      if (BadapplesState.List[name]) then
        -- We have a Badapple in the target, so we change the color
        -- only if the player is not attackable by us (i.e., they are
        -- not yet flagged) so as to leave the normal color clues for
        -- PvP combat.
        if (not UnitCanAttack("player", "target")) then
          TargetFrameNameBackground:SetVertexColor(0.9*_highlightColors.r, 0.9*_highlightColors.g, 0.9*_highlightColors.b);
        end
      end
    end
  end
end


------------------------------------------------------------------------------
-- Hook function for GameTooltip_UnitColor
------------------------------------------------------------------------------
function Badapples.GameTooltip_UnitColor(unit)
  -- Determines the color to set the player's name in the Game tooltip
  -- display.  In this case if we don't set the color ourselves we drop
  -- through and call the original function.
  if (UnitIsPlayer(unit)) then
    local name = UnitName(unit);
    if (name and (name ~= "")) then
      name = Badapples.FormatName(name);
      if (BadapplesState.List[name]) then
        -- We have a Badapple in the target, so we change the color
        -- only if the player is not attackable by us (i.e., they are
        -- not yet flagged) so as to leave the normal color clues for
        -- PvP combat.
        if (not UnitCanAttack("player", unit)) then
          return _highlightColors.r, _highlightColors.g, _highlightColors.b;
        end
      end
    end
  end
  return _original_GameTooltip_UnitColor(unit);
end


------------------------------------------------------------------------------
-- Hook function for ChatEdit_UpdateHeader
------------------------------------------------------------------------------
function Badapples.ChatEdit_UpdateHeader(editBox)
  -- Called after ChatEdit_UpdateHeader is called by hooksecurefunc, this
  -- function checks to see if the editbox header was updated for a new tell
  -- target and if this target is a player in our Badapples list then modify
  -- the format string before calling the original update function.
  local type = editBox:GetAttribute("chatType");
  if (type == "WHISPER") then
    local name = Badapples.FormatName(editBox:GetAttribute("tellTarget") or "");
    if (BadapplesState.List[name]) then
      -- Update header color and warn user about this badapple
      local header = _G[editBox:GetName().."Header"];
      if (header) then
        header:SetText(string.format(CHAT_WHISPER_SEND, BAD_ON..name..BAD_OFF));
      end
      Badapples.NotifyPlayer(BADAPPLES_TEXT.NOTIFY_BAD, name, BadapplesState.List[name].Reason or BADAPPLES_TEXT.NO_REASON);
    elseif (_ignoreList[name]) then
      -- Warn user about this ignored player
      Badapples.NotifyPlayer(BADAPPLES_TEXT.NOTIFY_IGNORE, name);
    end
  end
end


------------------------------------------------------------------------------
-- Frame and Button management functions
------------------------------------------------------------------------------
function Badapples.Frame_ListUpdate()
  -- Start with inits
  if (BadapplesFrame.SelectedName) then
    if (not BadapplesState.List[BadapplesFrame.SelectedName]) then
      BadapplesFrame.SelectedName = nil;
    end
  end
  if (_first == 1) then
    Badapples.SortList();
    _first = 0;
  end

  -- Format string for the total players text
  if (_listCount > 0) then
    if (_listCount == 1) then
      BadapplesFrameTotals:SetText("1마리의 무개념");
    else
      BadapplesFrameTotals:SetText(_listCount.."마리의 무개념들");
    end
  else
    BadapplesFrameTotals:SetText("무개념 리스트가 비어있습니다!");
  end

  -- Now get parameters for what to display
  local offset = FauxScrollFrame_GetOffset(BadapplesListScrollFrame);
  local name, reason;
  local nameText, reasonText;
  for i = 1, BADAPPLES_DISPLAY_COUNT do
    button = _G["BadapplesFrameButton"..i];
    nameText = _G["BadapplesFrameButton"..i.."Name"];
    reasonText = _G["BadapplesFrameButton"..i.."Reason"];
    index = i + offset;
    if (index <= _listCount) then
      name = _listSorted[index];
      button.Name = name;
      nameText:SetText(name);
      nameText:SetTextColor(_highlightColors.r, _highlightColors.g, _highlightColors.b);
      reason = BadapplesState.List[name].Reason;
      if (reason) then
        reasonText:SetText(reason);
      else
        reasonText:SetText("");
      end
      if (button.hasCursor) then
        button:OnEnterFunction();
      end
      if (BadapplesFrame.SelectedName == name) then
        button:LockHighlight();
      else
        button:UnlockHighlight();
      end
      button:Show();
    else
      nameText:SetText("");
      reasonText:SetText("");
      button:Hide();
    end
  end

  -- Update buttons and edit box for selected player
  if (BadapplesFrame.SelectedName) then
    BadapplesFrameRemoveButton:Enable();
    BadapplesFrameEditBox:SetMaxLetters(BADAPPLES_MAXIMUM_REASON_LENGTH);
    BadapplesFrameEditBox:ClearFocus();
    BadapplesFrameEditBox:Show();
    -- Use the non-printing character "\032" to force a
    -- text update of the editbox, and store the actual text we want
    -- displayed in the object to be handled when the OnTextChanged event
    -- fires.  This is simply to force the editbox to show all the text
    -- when a new text string is shorter than the previous one (see
    -- corresponding code in Badapples.xml).
    BadapplesFrameEditBox:SetText("\032");
    reason = BadapplesState.List[BadapplesFrame.SelectedName].Reason;
    if (reason) then
      BadapplesFrameEditBox.newText = reason;
    else
      BadapplesFrameEditBox.newText = "";
    end
  else
    BadapplesFrameRemoveButton:Disable();
    BadapplesFrameEditBox:SetText("");
    BadapplesFrameEditBox:Hide();
  end

  -- Control state of Add button based on what is targetted
  if (UnitIsPlayer("target")) then
    local name = UnitName("target");
    if (name and (name ~= "")) then
      name = Badapples.FormatName(name);
      if (BadapplesState.List[name]) then
        BadapplesFrameAddButton:Disable();
      else
        BadapplesFrameAddButton:Enable();
      end
    else
      BadapplesFrameAddButton:Enable();
    end
  else
    BadapplesFrameAddButton:Enable();
  end

  -- Update scrollbar
  FauxScrollFrame_Update(BadapplesListScrollFrame, _listCount, BADAPPLES_DISPLAY_COUNT, BADAPPLES_FRAME_SCROLL_HEIGHT);
end


function Badapples.Frame_DropDownOnClick(self)
  -- Called when a dropdown item is selected
  if (self.value == "WHO") then
    SendWho("n-"..BadapplesDropDown.name);
  elseif (self.value == "EDIT") then
    BadapplesFrameEditBox:SetFocus();
  elseif (self.value == "REMOVE") then
    -- We don't confirm removes done via right-click
    Badapples.Remove(BadapplesDropDown.name);
  end
end


function Badapples.Frame_DropdownInitialize(self)
  local info;
  -- First do title using Player name and entry date (if known)
  info = UIDropDownMenu_CreateInfo();
  info.text = BadapplesDropDown.name;
  info.isTitle = 1;
  info.notCheckable = 1;
  UIDropDownMenu_AddButton(info);

  -- Add button for Who command
  info = UIDropDownMenu_CreateInfo();
  info.text = "정보보기";
  info.notCheckable = 1;
  info.value = "WHO";
  info.owner = self;
  info.func = Badapples.Frame_DropDownOnClick;
  UIDropDownMenu_AddButton(info);

  -- Add button for Editing reason
  info = UIDropDownMenu_CreateInfo();
  info.text = "메모 편집";
  info.notCheckable = 1;
  info.value = "EDIT";
  info.owner = self;
  info.func = Badapples.Frame_DropDownOnClick;
  UIDropDownMenu_AddButton(info);

  -- Add button for removing Player
  info = UIDropDownMenu_CreateInfo();
  info.text = "목록에서 제거";
  info.notCheckable = 1;
  info.value = "REMOVE";
  info.func = Badapples.Frame_DropDownOnClick;
  UIDropDownMenu_AddButton(info);

  -- Finally add button for Cancel
  info = UIDropDownMenu_CreateInfo();
  info.text = "취소";
  info.notCheckable = 1;
  info.func = Badapples.Frame_DropDownOnClick;
  UIDropDownMenu_AddButton(info);
end

function Badapples.FrameButton_OnClick(self, button)
  if (button == "LeftButton") then
    HideDropDownMenu(1);
    BadapplesFrame.SelectedName = _G["BadapplesFrameButton"..self:GetID()].Name;
    Badapples.Frame_ListUpdate();
  else
    HideDropDownMenu(1);
    BadapplesFrame.SelectedName = _G["BadapplesFrameButton"..self:GetID()].Name;
    Badapples.Frame_ListUpdate();
    BadapplesDropDown.name = BadapplesFrame.SelectedName;
    BadapplesDropDown.initialize = Badapples.Frame_DropdownInitialize;
    BadapplesDropDown.displayMode = "MENU";
    ToggleDropDownMenu(1, nil, BadapplesDropDown, "cursor");
  end
end


function Badapples.ShowColorPicker()
  -- Open the ColorPickerFrame, making sure to close all other menus first
  CloseMenus();
  ColorPickerFrame.func = Badapples.SetHighLightColor;
  ColorPickerFrame.hasOpacity = nil;
  ColorPickerFrame.opacityFunc = nil;
  ColorPickerFrame.opacity = 0;
  ColorPickerFrame:SetColorRGB(_highlightColors.r, _highlightColors.g, _highlightColors.b);
  ColorPickerFrame.previousValues = {r = _highlightColors.r, g = _highlightColors.g, b = _highlightColors.b};
  ColorPickerFrame.cancelFunc = Badapples.SetHighLightColor;
  ShowUIPanel(ColorPickerFrame);
end


function Badapples.Frame_Add()
  -- If the current target is a suitable candidate, then prompt for
  -- confirmation, otherwise prompt for a player name.
  if (UnitIsPlayer("target")) then
    local name = UnitName("target");
    if (name and (name ~= "")) then
      name = Badapples.FormatName(name);
      if (not BadapplesState.List[name]) then
        -- Request confirmation before we add the targetted player
        local dialogFrame = StaticPopup_Show("BADAPPLE_ADD_CONFIRM", name);
        if (dialogFrame) then
          dialogFrame.data = name;
        end
        return;
      end
    end
  end
  StaticPopup_Show("BADAPPLE_ADD");
end


function Badapples.Frame_Remove()
  if (BadapplesFrame.SelectedName) then
    local dialogFrame = StaticPopup_Show("BADAPPLE_REMOVE_CONFIRM", BadapplesFrame.SelectedName);
    if (dialogFrame) then
      dialogFrame.data = BadapplesFrame.SelectedName;
    end
  end
end


function Badapples.Frame_EditReason()
  if (BadapplesFrame.SelectedName and BadapplesState.List[BadapplesFrame.SelectedName]) then
    local reason = BadapplesState.List[BadapplesFrame.SelectedName].Reason;
    if (reason ~= BadapplesFrameEditBox:GetText()) then
      Badapples.Add(BadapplesFrame.SelectedName.." "..BadapplesFrameEditBox:GetText());
    end
  end
  BadapplesFrameEditBox:ClearFocus();
end


function Badapples.Frame_EditBoxAddName(editBox)
  -- Called by the static popup dialog on accept or enter
  local name = editBox:GetText();
  if (name and (name ~= "")) then
    if (Badapples.Status(name, 1)) then
      BadapplesFrame.SelectedName = Badapples.FormatName(name);
      Badapples.Status(name);
    else
      Badapples.Add(name);
    end
  end
end


function Badapples.Frame_SetTab(action)
  if (_tabEnabled ~= action) then
    _tabEnabled = action;
    BadapplesFriendsFrameTab6:Show();
    if (FriendsFrame:IsShown()) then
      if (BadapplesFrame:IsShown()) then
        FriendsFrame.selectedTab = BADAPPLES_TAB_ID;
      end
      PanelTemplates_UpdateTabs(FriendsFrame);
      FriendsFrame_Update();
    end
    BadapplesState.Tab = action;
  end
end


function Badapples.Show()
  -- Forces the social window to show the Badapples list (if it isn't
  -- already visible)
  if (not BadapplesFrame:IsShown()) then
    if (not FriendsFrame:IsShown()) then
      ShowUIPanel(FriendsFrame);
    end

    FriendsFrame.selectedTab = BADAPPLES_TAB_ID;

    if (FriendsFrame:IsShown()) then
      PanelTemplates_UpdateTabs(FriendsFrame);
      FriendsFrame_Update();
    end
  end
end

------------------------------------------------------------------------------
-- Frame management hook functions
------------------------------------------------------------------------------
function Badapples.PanelTemplates_UpdateTabs(frame)
  -- Post-hooked to handle our additional tab
  if (frame == FriendsFrame) then
    if (frame.selectedTab == BADAPPLES_TAB_ID) then
      PanelTemplates_SelectTab(BadapplesFriendsFrameTab6);
    else
      PanelTemplates_DeselectTab(BadapplesFriendsFrameTab6);
    end
  end
end

function Badapples.FriendsFrame_Update()
  -- This post-hooked function checks for the currently selected
  -- FriendsFrame tab being the Badapples one, and handles that case.  It
  -- also makes sure that the tabs are correctly sized and positioned.
  if (FriendsFrame.selectedTab == BADAPPLES_TAB_ID) then
    --FriendsFrameTopLeft:SetTexture("Interface\\ClassTrainerFrame\\UI-ClassTrainer-TopLeft");
    --FriendsFrameTopRight:SetTexture("Interface\\ClassTrainerFrame\\UI-ClassTrainer-TopRight");
    --FriendsFrameBottomLeft:SetTexture("Interface\\FriendsFrame\\WhoFrame-BotLeft");
    --FriendsFrameBottomRight:SetTexture("Interface\\FriendsFrame\\WhoFrame-BotRight");
    FriendsFrameTitleText:SetText("Badapples List (무개념 목록)");
    FriendsFrame_ShowSubFrame("BadapplesFrame");
  end
end

------------------------------------------------------------------------------
-- OnLoad function
------------------------------------------------------------------------------
function Badapples.OnLoad(self)
  -- Record our frame pointer for later
  _thisFrame = self;

  -- Register for basic events
  _thisFrame:RegisterEvent("VARIABLES_LOADED");
  _thisFrame:RegisterEvent("PLAYER_LOGIN");
  _thisFrame:RegisterEvent("PLAYER_ENTERING_WORLD");
  _thisFrame:RegisterEvent("PLAYER_LEAVING_WORLD");

  -- Add our Badapples frame to the FriendsFrame subframe list for if the
  -- Badapples social tab is enabled.
  table.insert(FRIENDSFRAME_SUBFRAMES, "BadapplesFrame");

  -- Create a new BADAPPLE_INVITE popup for if we try and invite a Badapple
  StaticPopupDialogs["BADAPPLE_INVITE"] = _badapplesInvitePopup;

  -- Create a new BADAPPLE_ADD popup for asking the name of a player to add
  -- to the Badapples list, based on the ADD_IGNORE one
  StaticPopupDialogs["BADAPPLE_ADD"] = _badapplesAddPlayerPopup;

  -- Create new BADAPPLE_ADD_CONFIRM and BADAPPLE_REMOVE_CONFIRM popups for
  -- confirming adding or removing a player to or from the Badapples list
  StaticPopupDialogs["BADAPPLE_ADD_CONFIRM"] = _badapplesConfirmAddPopup;
  StaticPopupDialogs["BADAPPLE_REMOVE_CONFIRM"] = _badapplesConfirmRemovePopup;

  -- Create a new BADAPPLE_REMOVEALL popup for confirming deletion of all
  -- Badapple entries
  StaticPopupDialogs["BADAPPLE_REMOVEALL"] = _badapplesConfirmRemoveAllPopup;
end


function BadapplesTab_OnLoad(frame)
  -- Called for each tab frame as they are loaded, this function sets the
  -- tabs's appropriate text (long or short)
  frame:SetText(BADAPPLES_TEXT.BADAPPLES_TAB_SHORTNAME);
end


------------------------------------------------------------------------------
-- OnEvent and OnUpdate functions
------------------------------------------------------------------------------
function Badapples.OnEvent(event)
  if (event == "PLAYER_TARGET_CHANGED") then
    if (BadapplesFrame:IsShown()) then
      Badapples.Frame_ListUpdate();
    end
  elseif (event == "GROUP_ROSTER_UPDATE") then
    if (GetNumGroupMembers() == 0) then
      Badapples.CheckParty();
    else
      Badapples.CheckRaid();
    end

  elseif (event == "PLAYER_ENTERING_WORLD") then
    Badapples.RegisterEvents(1);
    Badapples.UpdateIgnoreList();
    if (_listCount > 0) then
      if (GetNumGroupMembers() > 0) then
        Badapples.CheckRaid();
      elseif (GetNumSubgroupMembers() > 0) then
        Badapples.CheckParty();
      end
    end

  elseif (event == "PLAYER_LEAVING_WORLD") then
    Badapples.RegisterEvents(0);
    _thisFrame:SetScript("OnUpdate", nil);

  elseif (event == "IGNORELIST_UPDATE") then
    Badapples.UpdateIgnoreList();

  elseif (event == "VARIABLES_LOADED") then
    _serverName = GetRealmName();
    Badapples.VariablesLoaded();
    if (_serverName and _playerName) then
      Badapples.PlayerLogin();
    end

  elseif (event == "PLAYER_LOGIN") then
    _playerName = UnitName("player");
    if (_serverName and _playerName) then
      Badapples.PlayerLogin();
    end

  end
end


function Badapples.OnUpdate()
  -- We only need this for knowing when to purge the party and raid data
  if (_partyMembersTimeout) then
    if (GetTime() > _partyMembersTimeout) then
      for name in pairs(_partyMembers) do
        if (_partyMembers[name] < 0) then
          _partyMembers[name] = nil;
        end
      end
      _partyMembersTimeout = nil;
    end
  end
  if (_raidMembersTimeout) then
    if (GetTime() > _raidMembersTimeout) then
      for name in pairs(_raidMembers) do
        if (_raidMembers[name] < 0) then
          _raidMembers[name] = nil;
        end
      end
      _raidMembersTimeout = nil;
    end
  end
  if (not _partyMembersTimeout and not _raidMembersTimeout) then
    _thisFrame:SetScript("OnUpdate", nil);
  end
end

------------------------------------------------------------------------------
-- Exported functions
------------------------------------------------------------------------------
function Badapples.CheckName(name)
  -- Returns the reason text, or BADAPPLES_TEXT.NO_REASON if the provided
  -- name is on the player's badapples list, or nil otherwise.
  if (_serverName and BadapplesState.Servers and BadapplesState.Servers[_serverName]) then
    if (name and (name ~= "")) then
      name = Badapples.FormatName(name);
      if (BadapplesState.List[name]) then
        local reason = BadapplesState.List[name].Reason;
        if (not reason) then
          reason = BADAPPLES_TEXT.NO_REASON;
        end
        return reason;
      end
    end
  end
end


function Badapples.GetColor()
  -- Returns the color used for indicating badapple names in the form:
  --   r, g, b, hex
  -- Where the hex parameter is the fully qualified color string (i.e., it
  -- starts with "|c").
  return _highlightColors.r, _highlightColors.g, _highlightColors.b, BAD_ON;
end


------------------------------------------------------------------------------
-- Whoami functions
------------------------------------------------------------------------------
function Badapples:GetName()
  return "Badapples";
end
