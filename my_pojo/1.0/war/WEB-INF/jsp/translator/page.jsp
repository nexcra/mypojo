<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<script type="text/javascript">
$(function() {
	$("#HASH_DO").button().click(function(e){
		$.send('/rest/translator/hash',$("#form").serialize(),function(message){
			$("#HASH_RESULT").text(message);
		});
	});
	$("#ENCRYPT_DO").button().click(function(e){
		$.send('/rest/translator/encrypt',$("#form").serialize(),function(message){
			$("#ENCRYPT_RESULT").text(message);
		});
	});
	$("#DECRYPT_DO").button().click(function(e){
		$.send('/rest/translator/decrypt',$("#form").serialize(),function(message){
			$("#DECRYPT_RESULT").text(message);
		});
	});
	$("#BID_DO").button().click(function(e){
		$.send('/rest/translator/randomBid',null,function(message){
			$("#BID_RESULT").text(message);
		});
	});
	$("#SID_DO").button().click(function(e){
		$.send('/rest/translator/randomSid',null,function(message){
			$("#SID_RESULT").text(message);
		});
	});
});
</script>	
<br>
<form id="form" >
<table>
	<thead>
		<tr>
			<th>이름</th>
			<th>String</th>
			<th>암호화에 사용될 문자</th>
			<th>Btn</th>
			<th>Result</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td>MD5 해시</td>
			<td><input name="HASH_VALUE" type="text"></td>
			<td></td>
			<td><input type="button" value="HASH_DO" id="HASH_DO"></td>
			<td id="HASH_RESULT" ></td>
		</tr>
		<tr>
			<td>Triple Des Encrypt</td>
			<td><input name="ENCRYPT_VALUE" type="text"></td>
			<td><input name="ENCRYPT_KEY" type="text"></td>
			<td><input type="button" value="ENCRYPT_DO" id="ENCRYPT_DO"></td>
			<td id="ENCRYPT_RESULT" ></td>
		</tr>
		<tr>
			<td>Triple Des Decrypt</td>
			<td><input name="DECRYPT_VALUE" type="text"></td>
			<td><input name="DECRYPT_KEY" type="text"></td>
			<td><input type="button" value="DECRYPT_DO" id="DECRYPT_DO"></td>
			<td id="DECRYPT_RESULT" ></td>
		</tr>
		<tr>
			<td>사업자 등록번호 생성</td>
			<td></td>
			<td></td>
			<td><input type="button" value="생성" id="BID_DO"></td>
			<td id="BID_RESULT" ></td>
		</tr>
		<tr>
			<td>주민등록번호생성</td>
			<td></td>
			<td></td>
			<td><input type="button" value="생성" id="SID_DO"></td>
			<td id="SID_RESULT" ></td>
		</tr>
	</tbody>
</table>
</form>
