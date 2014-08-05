local Util = require('ErwinsUtil.ErwinsUtil')
require('WowUtil.WowUtil')



local defaultMetatable = {}

defaultMetatable.__tostring = function(self)
  print(type(self))
  return 'asd'
end

local toSet = function(table)
  assert(table ~= nil)
  assert(type(table) == 'table')
  local map = {}
  setmetatable(map,defaultMetatable)
  for i,v in ipairs(table) do  
    map[v] = 1
  end
  return map
end

local set = toSet {'b','a','c'}
Util.info({'a','b','c',{d='123',{522,77}}},2)
Util.info(set)

print(set)


print(set['a'])




print(string.format('우리동네 %s 영감님','미친'))




--컬러가 적용되는 로그 / 확장까지
print(log)
log = {}
