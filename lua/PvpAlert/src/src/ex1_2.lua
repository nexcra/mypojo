local Util = require('ErwinsUtil.ErwinsUtil')

--메타테이블
local mt = {}
-- 추가로 __le __lt __eq __tostring
-- __pairs __ipairs 도 된다
mt.__add = function(a,b)
  return a.value + b.value
end

--추가로 newindex <-- 대입에 관여
mt.__index = function(_,key) --_,key
  local tt = {a=234,b='asd'}
  return tt[key]
end
mt.__newindex = function(_,key,value)
  print(key)
end


mt.__index = {a=234,b='asd'}




local aa = {value=10}
local bb = {value=13}

--주로 팩토리에서 메타 주입
setmetatable(aa,mt)
setmetatable(bb,mt)

print(aa+bb)
print(aa.b)
print(aa.x)

aa.tt = '123'
print(aa.tt)


local aa = Util.readOnly({a=123})
--aa.b= 12

print(_G['u'] == Util) --전역변수 저장공간

getfield = function(f)
  local v = _G
  for w in string.gmatch(f,"[\w_]+") do
    print('99')
    print(w)
    v = v[w]
  end
end

print(getfield('u.info'))
for w in string.gmatch('a_b 4_c',"[\w_]+") do
  print('~' .. w)
end

local a = {aa=234,cc = function(self) self.aa = self.aa + 1; print(self.aa) end }

a:cc()
a:cc()

--꼼수로 다중상속 가능 __index를 복잡하게 만들면..

--table.sort(table,comp)
local aa = {c=5,55,a=23,b=44,88,66} --,88,66
for key,v in pairs(aa) do
  print('pairs : ' .. key .. ' - ' .. v)
end
for i,e in ipairs(aa) do
  print('ipairs : ' .. e)
end

for k,v in Util.sortedPairs(aa) do
  print('pairsByKeys : ' .. k .. ' - ' .. v)
end

print(table.concat({2,3,4,44},','))

local a = 'asd'
print(a:upper())



