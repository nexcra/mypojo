print ('=== ErwinsUtil loaded ===')

-- =========== static 상수 ================
--local ErwinsUtil = {}
ErwinsUtil = {}

-- 간단한 테이블 내용물 검색기 -> 인라인 처리기 하나 만들자.
ErwinsUtil.info = function(table)
  if(type(table) ~= 'table') then
    print('- IS NOT TABLE : ' .. type(table) .. ' ' .. table)
    return
  end  
  print('- list size =  ' .. #table)
  for key in sortedPairs(table) do
    local value = table[key]
    local type = type(value)
    if(type=='function') then  value = 'function' end
    if(type=='table') then  value = 'table' end
    print('- ' .. key .. ' = ' .. value)
  end
end

--table.concat 대체용. table도 가능하게 만들어준다.
ErwinsUtil.join = function(...)
  local append = ''
  for i=0,arg.n do
    local obj = arg[i]
    --if(obj == nil ) append += 
    --if(type(obj))
  end 
end


--숫자 먼저 나오고난 후 문자를 정렬해서 리턴 
--나중에 리팩토링 하자.
ErwinsUtil.defaultComparator = function(a,b)
  local atype = type(a)
  local btype = type(b)
  if(atype == btype) then  return a < b end
  if(atype == 'number') then return true end
  return false
end

--정렬해서 돌린다.
ErwinsUtil.sortedPairs = function(t,comparator)
  local keyList = {}
  comparator = comparator or ErwinsUtil.defaultComparator
  for n in pairs(t) do keyList[#keyList+1] = n end
  table.sort(keyList,comparator)
  local i = 0
  return function()
    i = i +1
    return keyList[i],t[keyList[i]]
  end
end



--메타를 이용한 읽기전용 proxy 리턴
ErwinsUtil.readOnly = function(t)
  local proxy = {}
  local mt = {
    __index = t,
    __newindex = function() error('attemp to update a read-only table') end
  }
  setmetatable(proxy,mt)
  return proxy
end 

return ErwinsUtil