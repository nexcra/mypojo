require('ErwinsUtil.ErwinsUtil')

local function test(...)

  print('arg.n'..arg.n)
  print(arg[1])

  local sum = 0
  for i=0,10 do
    sum = sum + i
  end

  for i=0,10,1 do
    sum = sum + i
  end

  print(sum)
  print(i)

  if sum > 0 then
    print('ss')
  end

  while sum > 50 do
    sum = sum - 10
  end
  print(sum)

  local t1 = {1,2}
  local t2 = {a=1,b=2,c=3}
  t2.b = nil
  print(t1[1]) --인덱스가 1부터 시작함
  print(t2)
  for key in pairs(t2) do
    print(key..' = '..t2[key])
  end

  function t2:add()
    --self.a = 11
    print("애드0" .. t2.a)
  end

  t2.aa = function()
  --print("리무브 " .. t2.a)
  end


  t2.add()
  t2.aa()

  local aa = function(...)
    local a,b = ...  -- ... 로 받으면 가변인자이다.
    print (a)
    print (b)
  end
  aa(1,2)


  local text = [=[
    asdasd 이런거도됨
  ]=]
  
  print(text)
  
  --[===[
   주석처리
  ]===]
  
  print ( "10"  + 3) --자동변환 ㄷㄷ
  print ( tonumber("10")  + 3) --자동변환 ㄷㄷ
  print(type(nil))
  
  a={}
  a.a = a
  a.a.a.a = 3
  print(a.a)
  
  print(4 and 5)
  print(4 or 5) -- or 인경우 단축이 통용임으로 4까지만 읽고 5는 읽지 않는다.
  
  print(#{1,2,nil,3})
  print(#{1,2,3,nil})
  
  print(#{a=1,b='wer';3,7,[10]=878}) --list형식으로 된거만 #으로 간주
  
  a,b,c = 10,5
  a,b = b,a
  print (c) --무시된다
  print(a .. ' ' .. b)
  
  --error('alpha wow')
  --goto문 생략
  
  n = n or 3 --디폴트값 표시
  print(n)

  aa = function()
    return 'a','b' 
  end 

  local tt = {aa()}  --결과를 다중인자나 테이블에 저장가능
  print(tt[2])
  print((aa())) --()감싸면 하나만 리턴
  
  local a,b = unpack(tt)
  print(b)
  
  local aa = {{a=20},{a=50},{a=7}}
  print(aa[1])
  table.sort(aa , function (a,b) return a.a > b.a   end )
  print(aa[1])
  u.info(aa)
  
  --local 로 전역변수 샌드박싱이 가능
  
  local myItFactory = function(list)  --자신만의 이터레이터 만들고 for문 작성 가능. nil이 리턴되면 자동 스탑
    local i = 0
    return function() i = i+1; return list[i]  end 
  end
  
  for e in myItFactory({1,2,3}) do
    print(e)
  end 
  
  for i,v in ipairs {1,2,3} do
    print(i .. ' ' ..  v)
  end
  
  for i,v in next , {1,2,3} do
    print(i .. ' ' ..  v)
  end 
  
  print 'asd' --그루비처럼 가능
  
  f = loadstring("i = i + 1") --load 로 변경  그루비처럼 가능. 항상 전역변수만 사용함
  i = 0
  
  print(i)
  f()
  print(i)
  
  local aaa = assert(5)
  print (aaa)
  
  local ok,msg = pcall(function()
    assert( qweqwe ~= nil , {msg='no qweqwe error'})  
    end 
  )
  if(not ok) then u.info(msg) end
  
  local ok,msg = pcall(function()
    error({mm='no mouse 123'})  
    end 
  )
  if(not ok) then u.info(msg) end
  
  print(table.concat({'asd','34234'})) -- join기능. 이걸로 문자열 + 방지할것
  
  
  
  
  
  
  
  
  
end

test(1,3)
